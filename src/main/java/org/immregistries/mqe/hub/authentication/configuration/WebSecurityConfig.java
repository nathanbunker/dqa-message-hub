package org.immregistries.mqe.hub.authentication.configuration;

import org.immregistries.mqe.hub.authentication.filter.JWTAuthenticationFilter;
import org.immregistries.mqe.hub.authentication.filter.JWTLoginFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.authentication.session.CompositeSessionAuthenticationStrategy;
import org.springframework.security.web.authentication.session.ConcurrentSessionControlAuthenticationStrategy;
import org.springframework.security.web.authentication.session.RegisterSessionAuthenticationStrategy;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.security.web.session.HttpSessionEventPublisher;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	private AuthenticationManager authManager;
	@Autowired
	private JWTAuthenticationFilter authFilter;
	@Autowired
	private AuthenticationSuccessHandler successHandler;
	@Autowired
	private AuthenticationFailureHandler failureHandler;
	@Autowired
	private SessionRegistry sessionRegistry;
	@Autowired
	private SessionAuthenticationStrategy strategy;

	@Bean
	public SessionRegistry sessionRegistry() {
		return new SessionRegistryImpl();
	}

	@Bean
	public HttpSessionEventPublisher httpSessionEventPublisher() {
		return new HttpSessionEventPublisher();
	}

    @Bean
    protected JWTLoginFilter loginFilter(){
    	return new JWTLoginFilter("/mqe/api/login", authManager, successHandler, failureHandler, strategy);
    }

    @Bean
	public PasswordEncoder encoder() {
		return new BCryptPasswordEncoder();
	}
    
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.csrf().disable()
		.authorizeRequests()
			.antMatchers("/**/public/**").permitAll()
			.antMatchers("/**/api/login").permitAll()
			.antMatchers("/**/api/**").fullyAuthenticated()
			.and()
			.addFilterAfter(loginFilter(), UsernamePasswordAuthenticationFilter.class)
			.logout().logoutUrl("/**/api/logout").logoutSuccessHandler(new LogoutSuccessHandler() {
			@Override
			public void onLogoutSuccess(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Authentication authentication) throws IOException, ServletException {
				httpServletResponse.sendError(200, "logout success");
			}
		}).deleteCookies("JSESSIONID")
		.and().sessionManagement().maximumSessions(1).expiredUrl("/abc").sessionRegistry(this.sessionRegistry);
	}

	@Bean
	public CompositeSessionAuthenticationStrategy authStrategy() {
		ConcurrentSessionControlAuthenticationStrategy concurrentSessionControlAuthenticationStrategy = new ConcurrentSessionControlAuthenticationStrategy(
				this.sessionRegistry);
		concurrentSessionControlAuthenticationStrategy.setExceptionIfMaximumExceeded(true);
		concurrentSessionControlAuthenticationStrategy.setMaximumSessions(1);

		RegisterSessionAuthenticationStrategy registerSessionAuthenticationStrategy = new RegisterSessionAuthenticationStrategy(this.sessionRegistry);

		return new CompositeSessionAuthenticationStrategy(Arrays.asList(
				registerSessionAuthenticationStrategy,
				concurrentSessionControlAuthenticationStrategy
		));
	}

}
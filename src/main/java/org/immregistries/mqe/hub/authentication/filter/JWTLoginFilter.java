package org.immregistries.mqe.hub.authentication.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.immregistries.mqe.hub.authentication.model.AuthenticationToken;
import org.immregistries.mqe.hub.authentication.model.UserCredentials;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class JWTLoginFilter extends AbstractAuthenticationProcessingFilter {

	public JWTLoginFilter(String url, AuthenticationManager authManager, AuthenticationSuccessHandler successHandler, AuthenticationFailureHandler authenticationFailureHandler, SessionAuthenticationStrategy sessionStrategy) {
		super(new AntPathRequestMatcher(url));
		setAuthenticationManager(authManager);
		setAuthenticationSuccessHandler(successHandler);
		setAuthenticationFailureHandler(authenticationFailureHandler);
		setSessionAuthenticationStrategy(sessionStrategy);
	}

	@Override
	public Authentication attemptAuthentication(HttpServletRequest req, HttpServletResponse res) throws AuthenticationException, IOException {
		UserCredentials creds = new ObjectMapper().readValue(req.getInputStream(), UserCredentials.class);
		return getAuthenticationManager().authenticate(new AuthenticationToken(creds.getUsername(),creds.getFacilityId(), creds.getPassword()));
	}
	
}
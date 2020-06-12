package org.immregistries.mqe.hub.authentication.filter;

import org.immregistries.mqe.hub.authentication.model.AuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@Component
public class JWTAuthenticationFilter extends GenericFilterBean {

	protected static final String SESSION_AUTHENTICATION = "AUTH_TOKEN";

	@Override
	public void doFilter(ServletRequest req, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		filterChain.doFilter(req, response);
	}
}
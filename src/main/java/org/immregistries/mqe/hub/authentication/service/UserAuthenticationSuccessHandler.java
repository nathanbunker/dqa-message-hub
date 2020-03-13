package org.immregistries.mqe.hub.authentication.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.immregistries.mqe.hub.authentication.model.AuthenticationToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
@Component
public class UserAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
    private static final Logger logger = LoggerFactory.getLogger(UserAuthenticationSuccessHandler.class);

    @Autowired
    private ObjectMapper objectMapper;

    @Value("${mqe.loginTimeoutMinutes}")
    private int timeOutMinutes;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Authentication auth) throws IOException, ServletException {

    if(!(auth instanceof AuthenticationToken)) {
			throw new ServletException();
		}

    logger.warn("Logged in.  Time out in " + timeOutMinutes + " minutes");
    httpServletRequest.getSession().setMaxInactiveInterval(60*timeOutMinutes);
		AuthenticationToken token = (AuthenticationToken) auth;

		//-- Create response
        httpServletResponse.setContentType("application/json");
		objectMapper.writeValue(httpServletResponse.getWriter(), token.getPrincipal());
    }
}

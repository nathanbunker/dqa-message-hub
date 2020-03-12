package org.immregistries.mqe.hub.authentication.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.immregistries.mqe.hub.authentication.model.AuthenticationToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class UserAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Authentication auth) throws IOException, ServletException {
		if(!(auth instanceof AuthenticationToken)) {
			throw new ServletException();
		}
        httpServletRequest.getSession().setMaxInactiveInterval(60);
		AuthenticationToken token = (AuthenticationToken) auth;

		//-- Create response
        httpServletResponse.setContentType("application/json");
		objectMapper.writeValue(httpServletResponse.getWriter(), token.getPrincipal());
    }
}

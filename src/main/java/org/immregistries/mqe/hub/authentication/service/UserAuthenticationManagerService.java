package org.immregistries.mqe.hub.authentication.service;

import org.immregistries.mqe.hub.authentication.model.AuthenticationToken;
import org.immregistries.mqe.hub.authentication.model.UserCredentials;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserAuthenticationManagerService implements AuthenticationManager {

    @Autowired
    private UserJpaRepository userJpaRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        if(!(authentication instanceof AuthenticationToken)) {
            throw new AuthenticationCredentialsNotFoundException("Using incorrect authentication method");
        }
        AuthenticationToken token = (AuthenticationToken) authentication;

        UserCredentials credentials = userJpaRepository.findByUsername(token.getPrincipal().getUsername());
        if(credentials != null) {
            if(passwordEncoder.matches(token.getCredentials(), credentials.getPassword())) {
                token.setAuthenticated(true);
                token.setUser(credentials);
                token.eraseCredentials();
                return this.getAuthToken(credentials);
            } else {
                throw new BadCredentialsException("Invalid Password");
            }
        } else {
            String encryptedPassword = passwordEncoder.encode(token.getCredentials());
            UserCredentials createdUser = userJpaRepository.save(new UserCredentials(token.getPrincipal().getUsername(), token.getPrincipal().getFacilityId(), encryptedPassword));
            return this.getAuthToken(createdUser);
        }
    }

    private AuthenticationToken getAuthToken(UserCredentials credentials) {
        return new AuthenticationToken(credentials.getUsername(), credentials.getFacilityId());
    }
}

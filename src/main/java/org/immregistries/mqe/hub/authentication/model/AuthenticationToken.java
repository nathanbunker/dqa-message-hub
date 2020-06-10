package org.immregistries.mqe.hub.authentication.model;

import org.springframework.security.authentication.AbstractAuthenticationToken;

import java.util.Collection;
import java.util.Objects;

public class AuthenticationToken extends AbstractAuthenticationToken {

    private UserId user;
    private String password;

    public AuthenticationToken(String username, String facilityId, String password) {
        super((Collection)null);
        this.user = new UserId(username, facilityId);
        this.password = password;
    }

    public AuthenticationToken(String username, String facilityId) {
        super((Collection)null);
        this.user = new UserId(username, facilityId);
        this.setAuthenticated(true);
    }


    @Override
    public String getCredentials() {
        return password;
    }

    @Override
    public UserId getPrincipal() {
        return user;
    }

    @Override
    public void eraseCredentials() {
        super.eraseCredentials();
        this.password = "";
    }

    @Override
    public String getName() {
        return user.getUsername()+ "@" + user.getFacilityId();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        AuthenticationToken that = (AuthenticationToken) o;
        return user.equals(that.user);
    }

    public void setUser(UserId user) {
        this.user = user;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), user);
    }
}

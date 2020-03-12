package org.immregistries.mqe.hub.authentication.model;

import javax.persistence.Column;
import javax.persistence.Entity;

@Entity
public class UserCredentials extends UserId {

    @Column(name = "password", nullable = false)
    private String password;

    public UserCredentials() {
        super();
    }

    public UserCredentials(String user, String facilityId, String password) {
        super(user, facilityId);
        this.password = password;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}

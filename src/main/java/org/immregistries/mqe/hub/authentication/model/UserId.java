package org.immregistries.mqe.hub.authentication.model;

import javax.persistence.*;
import java.util.Objects;

@MappedSuperclass
public class UserId {

    @Id
    @Column(name = "username", updatable = false, nullable = false)
    protected String username;
    @Column(name = "facilityId", updatable = false, nullable = false)
    protected String facilityId;

    public UserId() {
    }


    public UserId(String username, String facilityId) {
        this.username = username;
        this.facilityId = facilityId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFacilityId() {
        return facilityId;
    }

    public void setFacilityId(String facilityId) {
        this.facilityId = facilityId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserId userId = (UserId) o;
        return username.equals(userId.username) &&
                facilityId.equals(userId.facilityId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username, facilityId);
    }
}

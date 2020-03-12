package org.immregistries.mqe.hub.authentication.model;

import javax.persistence.*;
import java.util.Objects;

@MappedSuperclass
public class UserId {
    @Id
    @SequenceGenerator(name = "USER_ID_GENERATOR", sequenceName = "USER_ID_SEQ")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "USER_ID_GENERATOR")
    protected Long id;
    @Column(name = "username", updatable = false, nullable = false)
    protected String username;
    @Column(name = "facilityId", updatable = false, nullable = false)
    protected String facilityId;

    public UserId() {
    }

    public UserId(String user, String facilityId) {
        this.username = user;
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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(username, facilityId);
    }
}

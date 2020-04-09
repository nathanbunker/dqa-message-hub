package org.immregistries.mqe.hub.authentication.service;

import org.immregistries.mqe.hub.authentication.model.UserCredentials;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserJpaRepository extends JpaRepository<UserCredentials, Long> {

    UserCredentials findByUsernameAndFacilityId(String username, String facilityId);
    UserCredentials findByUsername(String username);


}

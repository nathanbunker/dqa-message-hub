package org.immregistries.mqe.hub.settings;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DefaultDetectionGuidanceRepository extends JpaRepository<DetectionGuidance, String>  {
}

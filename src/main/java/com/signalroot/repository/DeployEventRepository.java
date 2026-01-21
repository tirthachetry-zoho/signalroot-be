package com.signalroot.repository;

import com.signalroot.entity.DeployEvent;
import com.signalroot.entity.Service;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DeployEventRepository extends JpaRepository<DeployEvent, UUID> {
    Optional<DeployEvent> findByExternalIdAndSource(String externalId, String source);
    
    @Query("SELECT d FROM DeployEvent d WHERE d.service = :service AND d.status = 'SUCCESS' " +
           "AND d.startedAt >= :since ORDER BY d.startedAt DESC")
    List<DeployEvent> findRecentSuccessfulDeploys(@Param("service") Service service, 
                                                  @Param("since") LocalDateTime since);
    
    @Query("SELECT d FROM DeployEvent d WHERE d.service = :service AND d.startedAt >= :since " +
           "ORDER BY d.startedAt DESC")
    List<DeployEvent> findByServiceAndStartedAtAfter(@Param("service") Service service, 
                                                     @Param("since") LocalDateTime since);
    
    List<DeployEvent> findByServiceOrderByStartedAtDesc(Service service);
}

package com.signalroot.repository;

import com.signalroot.entity.Alert;
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
public interface AlertRepository extends JpaRepository<Alert, UUID> {
    Optional<Alert> findByExternalIdAndSource(String externalId, String source);
    
    List<Alert> findByServiceAndStatusOrderByReceivedAtDesc(Service service, Alert.AlertStatus status);
    
    @Query("SELECT a FROM Alert a WHERE a.service = :service AND a.severity = :severity " +
           "AND a.createdAt >= :since ORDER BY a.createdAt DESC")
    List<Alert> findSimilarAlerts(@Param("service") Service service, 
                                 @Param("severity") Alert.AlertSeverity severity,
                                 @Param("since") LocalDateTime since);
    
    List<Alert> findByServiceOrderByReceivedAtDesc(Service service);
}

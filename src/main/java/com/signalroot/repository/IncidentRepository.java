package com.signalroot.repository;

import com.signalroot.entity.Alert;
import com.signalroot.entity.Incident;
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
public interface IncidentRepository extends JpaRepository<Incident, UUID> {
    List<Incident> findByServiceOrderByStartedAtDesc(Service service);
    
    List<Incident> findByStatusOrderByStartedAtDesc(Incident.IncidentStatus status);
    
    List<Incident> findByServiceAndStatusOrderByStartedAtDesc(Service service, Incident.IncidentStatus status);
    
    Optional<Incident> findByAlert(Alert alert);
    
    @Query("SELECT i FROM Incident i WHERE i.service = :service AND i.severity = :severity " +
           "ORDER BY i.startedAt DESC")
    List<Incident> findByServiceAndSeverityOrderByStartedAtDesc(@Param("service") Service service, 
                                                              @Param("severity") Alert.AlertSeverity severity);
    
    @Query("SELECT i FROM Incident i WHERE i.service = :service AND i.severity = :severity " +
           "AND i.status != 'RESOLVED' AND i.createdAt >= :since ORDER BY i.createdAt DESC")
    List<Incident> findSimilarActiveIncidents(@Param("service") Service service, 
                                            @Param("severity") Alert.AlertSeverity severity,
                                            @Param("since") LocalDateTime since);
    
    @Query("SELECT i FROM Incident i WHERE i.service = :service AND i.severity = :severity " +
           "AND i.createdAt >= :since ORDER BY i.createdAt DESC")
    List<Incident> findSimilarIncidents(@Param("service") Service service, 
                                       @Param("severity") Alert.AlertSeverity severity,
                                       @Param("since") LocalDateTime since);
}

package com.signalroot.entity;

import com.signalroot.entity.Alert.AlertSeverity;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "incidents")
public class Incident {
    @Id
    @Column(name = "id", columnDefinition = "BINARY(16)")
    private UUID id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_id", nullable = false)
    private Service service;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "alert_id", nullable = false)
    private Alert alert;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "related_deploy_event_id")
    private DeployEvent relatedDeployEvent;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "similar_incident_id")
    private Incident similarIncident;
    
    @Column(name = "title", nullable = false)
    private String title;
    
    @Column(name = "suggested_checks", columnDefinition = "TEXT")
    private String suggestedChecks;
    
    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private IncidentStatus status;
    
    @Column(name = "severity", nullable = false)
    @Enumerated(EnumType.STRING)
    private AlertSeverity severity;
    
    @Column(name = "started_at", nullable = false)
    private LocalDateTime startedAt;
    
    @PrePersist
    protected void onCreate() {
        if (id == null) {
            id = UUID.randomUUID();
        }
        if (startedAt == null) {
            startedAt = LocalDateTime.now();
        }
    }
    
    public Incident() {}
    
    public Incident(Service service, Alert alert, String title, AlertSeverity severity, IncidentStatus status) {
        this.service = service;
        this.alert = alert;
        this.title = title;
        this.severity = severity;
        this.status = status;
    }
    
    public UUID getId() {
        return id;
    }
    
    public void setId(UUID id) {
        this.id = id;
    }
    
    public Service getService() {
        return service;
    }
    
    public void setService(Service service) {
        this.service = service;
    }
    
    public Alert getAlert() {
        return alert;
    }
    
    public void setAlert(Alert alert) {
        this.alert = alert;
    }
    
    public DeployEvent getRelatedDeployEvent() {
        return relatedDeployEvent;
    }
    
    public void setRelatedDeployEvent(DeployEvent relatedDeployEvent) {
        this.relatedDeployEvent = relatedDeployEvent;
    }
    
    public Incident getSimilarIncident() {
        return similarIncident;
    }
    
    public void setSimilarIncident(Incident similarIncident) {
        this.similarIncident = similarIncident;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getSuggestedChecks() {
        return suggestedChecks;
    }
    
    public void setSuggestedChecks(String suggestedChecks) {
        this.suggestedChecks = suggestedChecks;
    }
    
    public IncidentStatus getStatus() {
        return status;
    }
    
    public void setStatus(IncidentStatus status) {
        this.status = status;
    }
    
    public AlertSeverity getSeverity() {
        return severity;
    }
    
    public void setSeverity(AlertSeverity severity) {
        this.severity = severity;
    }
    
    public LocalDateTime getStartedAt() {
        return startedAt;
    }
    
    public void setStartedAt(LocalDateTime startedAt) {
        this.startedAt = startedAt;
    }
    
    public enum IncidentStatus {
        ACTIVE, RESOLVED, ACKNOWLEDGED
    }
}

package com.signalroot.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "alerts")
public class Alert {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @Column(nullable = false)
    private String externalId;
    
    @Column(nullable = false)
    private String source;
    
    @ManyToOne(fetch = FetchType.LAZY)
    private Service service;
    
    @ManyToOne(fetch = FetchType.LAZY)
    private Organization organization;
    
    @Enumerated(EnumType.STRING)
    private AlertSeverity severity;
    
    @Column(nullable = false)
    private String title;
    
    @Enumerated(EnumType.STRING)
    private AlertStatus status;
    
    @Column(nullable = false)
    private LocalDateTime startedAt;
    
    @Column(nullable = false)
    private LocalDateTime createdAt;
    
    @Column(nullable = false)
    private LocalDateTime updatedAt;
    
    // Enums
    public enum AlertSeverity {
        CRITICAL, HIGH, MEDIUM, LOW
    }
    
    public enum AlertStatus {
        FIRING, RESOLVED
    }
    
    // Constructors
    public Alert() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.startedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
    
    // Getters and Setters
    public UUID getId() {
        return id;
    }
    
    public void setId(UUID id) {
        this.id = id;
    }
    
    public String getExternalId() {
        return externalId;
    }
    
    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }
    
    public String getSource() {
        return source;
    }
    
    public void setSource(String source) {
        this.source = source;
    }
    
    public Service getService() {
        return service;
    }
    
    public void setService(Service service) {
        this.service = service;
    }
    
    public Organization getOrganization() {
        return organization;
    }
    
    public void setOrganization(Organization organization) {
        this.organization = organization;
    }
    
    public AlertSeverity getSeverity() {
        return severity;
    }
    
    public void setSeverity(AlertSeverity severity) {
        this.severity = severity;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public AlertStatus getStatus() {
        return status;
    }
    
    public void setStatus(AlertStatus status) {
        this.status = status;
    }
    
    public LocalDateTime getStartedAt() {
        return startedAt;
    }
    
    public void setStartedAt(LocalDateTime startedAt) {
        this.startedAt = startedAt;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}

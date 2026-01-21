package com.signalroot.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "deploy_events")
public class DeployEvent {
    @Id
    @Column(name = "id", columnDefinition = "BINARY(16)")
    private UUID id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_id", nullable = false)
    private Service service;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_id", nullable = false)
    private Organization organization;
    
    @Column(name = "source", nullable = false)
    private String source;
    
    @Column(name = "external_id")
    private String externalId;
    
    @Column(name = "version")
    private String version;
    
    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private DeployStatus status;
    
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
    
    public DeployEvent() {}
    
    public DeployEvent(Service service, String source, DeployStatus status, LocalDateTime startedAt) {
        this.service = service;
        this.source = source;
        this.status = status;
        this.startedAt = startedAt;
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
    
    public Organization getOrganization() {
        return organization;
    }
    
    public void setOrganization(Organization organization) {
        this.organization = organization;
    }
    
    public String getSource() {
        return source;
    }
    
    public void setSource(String source) {
        this.source = source;
    }
    
    public String getExternalId() {
        return externalId;
    }
    
    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }
    
    public String getVersion() {
        return version;
    }
    
    public void setVersion(String version) {
        this.version = version;
    }
    
    public DeployStatus getStatus() {
        return status;
    }
    
    public void setStatus(DeployStatus status) {
        this.status = status;
    }
    
    public LocalDateTime getStartedAt() {
        return startedAt;
    }
    
    public void setStartedAt(LocalDateTime startedAt) {
        this.startedAt = startedAt;
    }
    
    public enum DeployStatus {
        IN_PROGRESS, SUCCESS, FAILURE, PARTIAL
    }
}

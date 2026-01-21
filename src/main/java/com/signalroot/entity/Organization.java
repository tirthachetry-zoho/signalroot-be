package com.signalroot.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "organizations")
public class Organization {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @Column(unique = true, nullable = false)
    private String organizationKey;
    
    @Column(nullable = false)
    private String name;
    
    @Column(unique = true, nullable = false)
    private String webhookSecret;
    
    @Column(nullable = false)
    private LocalDateTime createdAt;
    
    @Column(nullable = false)
    private LocalDateTime updatedAt;
    
    // Slack integration
    @Column(name = "slack_access_token")
    private String slackAccessToken;
    
    @Column(name = "slack_workspace_id")
    private String slackWorkspaceId;
    
    @Column(name = "slack_workspace_name")
    private String slackWorkspaceName;
    
    @Column(name = "slack_channel")
    private String slackChannel = "#alerts";
    
    @Column(name = "slack_connected_at")
    private LocalDateTime slackConnectedAt;
    
    // Email integration
    @Column(name = "email_sender")
    private String emailSender;
    
    @Column(name = "email_verified")
    private Boolean emailVerified = false;
    
    // Integration status
    @Column(name = "slack_enabled")
    private Boolean slackEnabled = false;
    
    @Column(name = "email_enabled")
    private Boolean emailEnabled = false;
    
    // Constructors
    public Organization() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    public Organization(String organizationKey, String name) {
        this();
        this.organizationKey = organizationKey;
        this.name = name;
        this.webhookSecret = generateWebhookSecret();
    }
    
    private String generateWebhookSecret() {
        return UUID.randomUUID().toString().replace("-", "");
    }
    
    // Getters and Setters
    public UUID getId() {
        return id;
    }
    
    public void setId(UUID id) {
        this.id = id;
    }
    
    public String getOrganizationKey() {
        return organizationKey;
    }
    
    public void setOrganizationKey(String organizationKey) {
        this.organizationKey = organizationKey;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getWebhookSecret() {
        return webhookSecret;
    }
    
    public void setWebhookSecret(String webhookSecret) {
        this.webhookSecret = webhookSecret;
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
    
    public String getSlackAccessToken() {
        return slackAccessToken;
    }
    
    public void setSlackAccessToken(String slackAccessToken) {
        this.slackAccessToken = slackAccessToken;
    }
    
    public String getSlackWorkspaceId() {
        return slackWorkspaceId;
    }
    
    public void setSlackWorkspaceId(String slackWorkspaceId) {
        this.slackWorkspaceId = slackWorkspaceId;
    }
    
    public String getSlackWorkspaceName() {
        return slackWorkspaceName;
    }
    
    public void setSlackWorkspaceName(String slackWorkspaceName) {
        this.slackWorkspaceName = slackWorkspaceName;
    }
    
    public String getSlackChannel() {
        return slackChannel;
    }
    
    public void setSlackChannel(String slackChannel) {
        this.slackChannel = slackChannel;
    }
    
    public LocalDateTime getSlackConnectedAt() {
        return slackConnectedAt;
    }
    
    public void setSlackConnectedAt(LocalDateTime slackConnectedAt) {
        this.slackConnectedAt = slackConnectedAt;
    }
    
    public String getEmailSender() {
        return emailSender;
    }
    
    public void setEmailSender(String emailSender) {
        this.emailSender = emailSender;
    }
    
    public Boolean getEmailVerified() {
        return emailVerified;
    }
    
    public void setEmailVerified(Boolean emailVerified) {
        this.emailVerified = emailVerified;
    }
    
    public Boolean getSlackEnabled() {
        return slackEnabled;
    }
    
    public void setSlackEnabled(Boolean slackEnabled) {
        this.slackEnabled = slackEnabled;
    }
    
    public Boolean getEmailEnabled() {
        return emailEnabled;
    }
    
    public void setEmailEnabled(Boolean emailEnabled) {
        this.emailEnabled = emailEnabled;
    }
    
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}

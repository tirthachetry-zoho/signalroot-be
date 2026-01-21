package com.signalroot.service;

import com.signalroot.entity.Organization;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service("mockOrganizationService")
public class MockOrganizationService implements OrganizationServiceInterface {
    
    private static final Logger logger = LoggerFactory.getLogger(MockOrganizationService.class);
    
    private final Map<String, Organization> organizations = new ConcurrentHashMap<>();
    
    public MockOrganizationService() {
        initializeMockOrganizations();
    }
    
    private void initializeMockOrganizations() {
        // Create test organizations
        Organization acmeCorp = new Organization();
        acmeCorp.setId(UUID.fromString("550e8400-e29b-41d4-a716-446655440000"));
        acmeCorp.setOrganizationKey("acme-corp");
        acmeCorp.setName("Acme Corp");
        acmeCorp.setWebhookSecret("a1b2c3d4e5f6g7h8i9j0k1l2m3n4o5p6");
        acmeCorp.setCreatedAt(LocalDateTime.now());
        acmeCorp.setUpdatedAt(LocalDateTime.now());
        organizations.put("acme-corp", acmeCorp);
        
        Organization techStart = new Organization();
        techStart.setId(UUID.fromString("660f9511-f3ac-52e5-b827-557766551111"));
        techStart.setOrganizationKey("tech-start");
        techStart.setName("Tech Start Inc");
        techStart.setWebhookSecret("b2c3d4e5f6g7h8i9j0k1l2m3n4o5p6q7");
        techStart.setCreatedAt(LocalDateTime.now());
        techStart.setUpdatedAt(LocalDateTime.now());
        organizations.put("tech-start", techStart);
        
        Organization dataFlow = new Organization();
        dataFlow.setId(UUID.fromString("77106222-f4bd-63f6-c938-668877662222"));
        dataFlow.setOrganizationKey("dataflow");
        dataFlow.setName("DataFlow Systems");
        dataFlow.setWebhookSecret("c3d4e5f6g7h8i9j0k1l2m3n4o5p6q7r8");
        dataFlow.setCreatedAt(LocalDateTime.now());
        dataFlow.setUpdatedAt(LocalDateTime.now());
        organizations.put("dataflow", dataFlow);
        
        logger.info("Initialized {} mock organizations", organizations.size());
    }
    
    public Organization createOrganization(String name) {
        logger.info("Creating new organization: {}", name);
        
        // Generate unique organization key
        String organizationKey = generateOrganizationKey(name);
        
        Organization organization = new Organization();
        organization.setId(UUID.randomUUID());
        organization.setOrganizationKey(organizationKey);
        organization.setName(name);
        organization.setWebhookSecret(generateWebhookSecret());
        organization.setCreatedAt(LocalDateTime.now());
        organization.setUpdatedAt(LocalDateTime.now());
        
        organizations.put(organizationKey, organization);
        logger.info("Created organization: {} with key: {}", organization.getName(), organization.getOrganizationKey());
        
        return organization;
    }
    
    public Organization findByOrganizationKey(String organizationKey) {
        return organizations.get(organizationKey);
    }
    
    public Organization findById(UUID id) {
        return organizations.values().stream()
            .filter(org -> org.getId().equals(id))
            .findFirst()
            .orElse(null);
    }
    
    public List<Organization> findAll() {
        return new ArrayList<>(organizations.values());
    }
    
    public Organization updateOrganization(UUID id, String name) {
        Organization organization = findById(id);
        if (organization == null) {
            throw new IllegalArgumentException("Organization not found: " + id);
        }
        
        organization.setName(name);
        organization.setUpdatedAt(LocalDateTime.now());
        
        return organization;
    }
    
    public void deleteOrganization(UUID id) {
        Organization organization = findById(id);
        if (organization == null) {
            throw new IllegalArgumentException("Organization not found: " + id);
        }
        
        organizations.remove(organization.getOrganizationKey());
        logger.info("Deleted organization: {}", id);
    }
    
    public boolean existsByOrganizationKey(String organizationKey) {
        return organizations.containsKey(organizationKey);
    }
    
    private String generateOrganizationKey(String name) {
        // Convert name to lowercase, replace spaces with hyphens, add random suffix
        String base = name.toLowerCase().replaceAll("[^a-z0-9]", "-");
        
        // Ensure uniqueness
        String key = base;
        int counter = 1;
        
        while (existsByOrganizationKey(key)) {
            key = base + "-" + counter;
            counter++;
        }
        
        return key;
    }
    
    private String generateWebhookSecret() {
        return UUID.randomUUID().toString().replace("-", "");
    }
    
    public Organization connectSlack(UUID organizationId, String accessToken, String workspaceId, String workspaceName) {
        Organization organization = findById(organizationId);
        if (organization == null) {
            throw new IllegalArgumentException("Organization not found: " + organizationId);
        }
        
        organization.setSlackAccessToken(accessToken);
        organization.setSlackWorkspaceId(workspaceId);
        organization.setSlackWorkspaceName(workspaceName);
        organization.setSlackConnectedAt(LocalDateTime.now());
        organization.setSlackEnabled(true);
        organization.setUpdatedAt(LocalDateTime.now());
        
        logger.info("Connected Slack workspace: {} to organization: {}", workspaceName, organization.getName());
        
        return organization;
    }
    
    public Organization disconnectSlack(UUID organizationId) {
        Organization organization = findById(organizationId);
        if (organization == null) {
            throw new IllegalArgumentException("Organization not found: " + organizationId);
        }
        
        organization.setSlackAccessToken(null);
        organization.setSlackWorkspaceId(null);
        organization.setSlackWorkspaceName(null);
        organization.setSlackConnectedAt(null);
        organization.setSlackEnabled(false);
        organization.setUpdatedAt(LocalDateTime.now());
        
        logger.info("Disconnected Slack from organization: {}", organization.getName());
        
        return organization;
    }
    
    public Organization updateSlackChannel(UUID organizationId, String channel) {
        Organization organization = findById(organizationId);
        if (organization == null) {
            throw new IllegalArgumentException("Organization not found: " + organizationId);
        }
        
        organization.setSlackChannel(channel);
        organization.setUpdatedAt(LocalDateTime.now());
        
        logger.info("Updated Slack channel to: {} for organization: {}", channel, organization.getName());
        
        return organization;
    }
}

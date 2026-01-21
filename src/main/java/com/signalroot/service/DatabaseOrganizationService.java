package com.signalroot.service;

import com.signalroot.entity.Organization;
import com.signalroot.repository.OrganizationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@ConditionalOnProperty(name = "signalroot.service.mode", havingValue = "real")
public class DatabaseOrganizationService implements OrganizationServiceInterface {
    
    private static final Logger logger = LoggerFactory.getLogger(DatabaseOrganizationService.class);
    
    @Autowired
    private OrganizationRepository organizationRepository;
    
    public Organization createOrganization(String name) {
        logger.info("Creating new organization: {}", name);
        
        // Generate unique organization key
        String organizationKey = generateOrganizationKey(name);
        
        Organization organization = new Organization(organizationKey, name);
        
        Organization saved = organizationRepository.save(organization);
        logger.info("Created organization: {} with key: {}", saved.getName(), saved.getOrganizationKey());
        
        return saved;
    }
    
    public Organization findByOrganizationKey(String organizationKey) {
        Optional<Organization> org = organizationRepository.findByOrganizationKey(organizationKey);
        return org.orElse(null);
    }
    
    public Organization findById(UUID id) {
        return organizationRepository.findById(id).orElse(null);
    }
    
    public List<Organization> findAll() {
        return organizationRepository.findAll();
    }
    
    public Organization updateOrganization(UUID id, String name) {
        Optional<Organization> orgOpt = organizationRepository.findById(id);
        if (orgOpt.isEmpty()) {
            throw new IllegalArgumentException("Organization not found: " + id);
        }
        
        Organization organization = orgOpt.get();
        organization.setName(name);
        
        return organizationRepository.save(organization);
    }
    
    public void deleteOrganization(UUID id) {
        if (!organizationRepository.existsById(id)) {
            throw new IllegalArgumentException("Organization not found: " + id);
        }
        
        organizationRepository.deleteById(id);
        logger.info("Deleted organization: {}", id);
    }
    
    public boolean existsByOrganizationKey(String organizationKey) {
        return organizationRepository.existsByOrganizationKey(organizationKey);
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
    
    public Organization connectSlack(UUID organizationId, String accessToken, String workspaceId, String workspaceName) {
        Optional<Organization> orgOpt = organizationRepository.findById(organizationId);
        if (orgOpt.isEmpty()) {
            throw new IllegalArgumentException("Organization not found: " + organizationId);
        }
        
        Organization organization = orgOpt.get();
        organization.setSlackAccessToken(accessToken);
        organization.setSlackWorkspaceId(workspaceId);
        organization.setSlackWorkspaceName(workspaceName);
        organization.setSlackConnectedAt(java.time.LocalDateTime.now());
        organization.setSlackEnabled(true);
        
        Organization saved = organizationRepository.save(organization);
        logger.info("Connected Slack workspace: {} to organization: {}", workspaceName, saved.getName());
        
        return saved;
    }
    
    public Organization disconnectSlack(UUID organizationId) {
        Optional<Organization> orgOpt = organizationRepository.findById(organizationId);
        if (orgOpt.isEmpty()) {
            throw new IllegalArgumentException("Organization not found: " + organizationId);
        }
        
        Organization organization = orgOpt.get();
        organization.setSlackAccessToken(null);
        organization.setSlackWorkspaceId(null);
        organization.setSlackWorkspaceName(null);
        organization.setSlackConnectedAt(null);
        organization.setSlackEnabled(false);
        
        Organization saved = organizationRepository.save(organization);
        logger.info("Disconnected Slack from organization: {}", saved.getName());
        
        return saved;
    }
    
    public Organization updateSlackChannel(UUID organizationId, String channel) {
        Optional<Organization> orgOpt = organizationRepository.findById(organizationId);
        if (orgOpt.isEmpty()) {
            throw new IllegalArgumentException("Organization not found: " + organizationId);
        }
        
        Organization organization = orgOpt.get();
        organization.setSlackChannel(channel);
        
        Organization saved = organizationRepository.save(organization);
        logger.info("Updated Slack channel to: {} for organization: {}", channel, saved.getName());
        
        return saved;
    }
}

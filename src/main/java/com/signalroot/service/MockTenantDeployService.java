package com.signalroot.service;

import com.signalroot.entity.DeployEvent;
import com.signalroot.entity.Organization;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service("mockTenantDeployService")
public class MockTenantDeployService implements TenantDeployServiceInterface {
    
    private static final Logger logger = LoggerFactory.getLogger(MockTenantDeployService.class);
    private final Map<String, DeployEvent> deployStore = new ConcurrentHashMap<>();
    
    @Autowired
    private IdempotencyService idempotencyService;
    
    public void processGitHubDeploy(Map<String, Object> payload, Organization organization) {
        logger.info("🐙 Processing GitHub deploy for organization: {}", organization.getName());
        
        try {
            Map<String, Object> deployment = (Map<String, Object>) payload.get("deployment");
            String externalId = (String) deployment.get("id");
            String source = "github";
            
            // Idempotency check
            if (!idempotencyService.shouldProcessWebhook(externalId, source, externalId)) {
                return;
            }
            
            // Mark webhook as processed
            idempotencyService.markWebhookProcessed(externalId, source, externalId);
            
            // Create mock deploy event
            DeployEvent deployEvent = new DeployEvent();
            deployEvent.setId(UUID.randomUUID());
            deployEvent.setOrganization(organization);
            deployEvent.setSource(source);
            deployEvent.setExternalId(externalId);
            deployEvent.setVersion((String) deployment.get("environment"));
            
            Map<String, Object> status = (Map<String, Object>) deployment.get("status");
            deployEvent.setStatus(mapGitHubStatus((String) status.get("state")));
            
            String createdAt = (String) deployment.get("created_at");
            deployEvent.setStartedAt(LocalDateTime.parse(createdAt.replace("Z", "")));
            
            // Store in mock database
            deployStore.put(source + ":" + externalId, deployEvent);
            
            logger.info("✅ GitHub deploy processed: {} for organization: {}", externalId, organization.getName());
            
        } catch (Exception e) {
            logger.error("❌ Failed to process GitHub deploy for organization: {}", organization.getName(), e);
            throw e;
        }
    }
    
    public void processJenkinsDeploy(Map<String, Object> payload, Organization organization) {
        logger.info("🔧 Processing Jenkins deploy for organization: {}", organization.getName());
        
        try {
            Map<String, Object> build = (Map<String, Object>) payload.get("build");
            String externalId = (String) build.get("number") + "-" + (String) build.get("timestamp");
            String source = "jenkins";
            
            // Idempotency check
            if (!idempotencyService.shouldProcessWebhook(externalId, source, externalId)) {
                return;
            }
            
            // Mark webhook as processed
            idempotencyService.markWebhookProcessed(externalId, source, externalId);
            
            // Create mock deploy event
            DeployEvent deployEvent = new DeployEvent();
            deployEvent.setId(UUID.randomUUID());
            deployEvent.setOrganization(organization);
            deployEvent.setSource(source);
            deployEvent.setExternalId(externalId);
            
            Map<String, Object> artifact = (Map<String, Object>) build.get("artifact");
            deployEvent.setVersion((String) artifact.get("version"));
            deployEvent.setStatus(mapJenkinsStatus((String) build.get("status")));
            
            String timestamp = (String) build.get("timestamp");
            deployEvent.setStartedAt(LocalDateTime.parse(timestamp.replace("Z", "")));
            
            // Store in mock database
            deployStore.put(source + ":" + externalId, deployEvent);
            
            logger.info("✅ Jenkins deploy processed: {} for organization: {}", externalId, organization.getName());
            
        } catch (Exception e) {
            logger.error("❌ Failed to process Jenkins deploy for organization: {}", organization.getName(), e);
            throw e;
        }
    }
    
    private DeployEvent.DeployStatus mapGitHubStatus(String state) {
        return switch (state.toLowerCase()) {
            case "success" -> DeployEvent.DeployStatus.SUCCESS;
            case "failure", "error" -> DeployEvent.DeployStatus.FAILURE;
            case "pending" -> DeployEvent.DeployStatus.IN_PROGRESS;
            default -> DeployEvent.DeployStatus.IN_PROGRESS;
        };
    }
    
    private DeployEvent.DeployStatus mapJenkinsStatus(String status) {
        return switch (status.toUpperCase()) {
            case "SUCCESS" -> DeployEvent.DeployStatus.SUCCESS;
            case "FAILURE", "ABORTED" -> DeployEvent.DeployStatus.FAILURE;
            case "UNSTABLE" -> DeployEvent.DeployStatus.PARTIAL;
            default -> DeployEvent.DeployStatus.IN_PROGRESS;
        };
    }
}

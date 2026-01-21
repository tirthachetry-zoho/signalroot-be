package com.signalroot.service;

import com.signalroot.entity.DeployEvent;
import com.signalroot.entity.Organization;
import com.signalroot.repository.DeployEventRepository;
import com.signalroot.repository.ServiceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Map;

@Service
@ConditionalOnProperty(name = "signalroot.service.mode", havingValue = "real")
public class TenantDeployService implements TenantDeployServiceInterface {
    
    private static final Logger logger = LoggerFactory.getLogger(TenantDeployService.class);
    
    @Autowired
    private DeployEventRepository deployEventRepository;
    
    @Autowired
    private ServiceRepository serviceRepository;
    
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
            
            // Extract repository name
            Map<String, Object> repository = (Map<String, Object>) payload.get("repository");
            String repositoryName = (String) repository.get("name");
            String serviceName = extractServiceNameFromRepo(repositoryName);
            
            // Find or create service
            com.signalroot.entity.Service service = findOrCreateService(serviceName, organization);
            
            // Create deploy event
            DeployEvent deployEvent = new DeployEvent();
            deployEvent.setService(service);
            deployEvent.setOrganization(organization);
            deployEvent.setSource(source);
            deployEvent.setExternalId(externalId);
            deployEvent.setVersion((String) deployment.get("environment"));
            
            Map<String, Object> status = (Map<String, Object>) deployment.get("status");
            deployEvent.setStatus(mapGitHubStatus((String) status.get("state")));
            
            String createdAt = (String) deployment.get("created_at");
            deployEvent.setStartedAt(LocalDateTime.parse(createdAt.replace("Z", "")));
            
            deployEventRepository.save(deployEvent);
            
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
            
            // Extract service name
            Map<String, Object> job = (Map<String, Object>) payload.get("job");
            String jobName = (String) job.get("name");
            String serviceName = extractServiceNameFromJob(jobName);
            
            // Find or create service
            com.signalroot.entity.Service service = findOrCreateService(serviceName, organization);
            
            // Create deploy event
            DeployEvent deployEvent = new DeployEvent();
            deployEvent.setService(service);
            deployEvent.setOrganization(organization);
            deployEvent.setSource(source);
            deployEvent.setExternalId(externalId);
            
            Map<String, Object> artifact = (Map<String, Object>) build.get("artifact");
            deployEvent.setVersion((String) artifact.get("version"));
            deployEvent.setStatus(mapJenkinsStatus((String) build.get("status")));
            
            String timestamp = (String) build.get("timestamp");
            deployEvent.setStartedAt(LocalDateTime.parse(timestamp.replace("Z", "")));
            
            deployEventRepository.save(deployEvent);
            
            logger.info("✅ Jenkins deploy processed: {} for organization: {}", externalId, organization.getName());
            
        } catch (Exception e) {
            logger.error("❌ Failed to process Jenkins deploy for organization: {}", organization.getName(), e);
            throw e;
        }
    }
    
    private com.signalroot.entity.Service findOrCreateService(String serviceName, Organization organization) {
        return serviceRepository.findByNameAndOrganization(serviceName, organization)
            .orElseGet(() -> {
                com.signalroot.entity.Service newService = new com.signalroot.entity.Service(serviceName, organization);
                return serviceRepository.save(newService);
            });
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
    
    private String extractServiceNameFromRepo(String repositoryName) {
        // Extract service name from repository name like "payment-service-api"
        if (repositoryName.contains("-")) {
            return repositoryName.split("-")[0].toLowerCase();
        }
        return repositoryName.toLowerCase();
    }
    
    private String extractServiceNameFromJob(String jobName) {
        // Extract service name from job name like "payment-service-deploy"
        if (jobName.contains("-")) {
            return jobName.split("-")[0].toLowerCase();
        }
        return jobName.toLowerCase();
    }
}

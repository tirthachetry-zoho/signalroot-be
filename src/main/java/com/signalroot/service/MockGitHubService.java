package com.signalroot.service;

import com.signalroot.dto.GitHubDeployWebhookDTO;
import com.signalroot.dto.JenkinsDeployWebhookDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

@Service
public class MockGitHubService {
    
    private static final Logger logger = LoggerFactory.getLogger(MockGitHubService.class);
    private final Map<String, GitHubDeploy> deployStore = new ConcurrentHashMap<>();
    
    public void processGitHubDeploy(GitHubDeployWebhookDTO webhook) {
        GitHubDeployWebhookDTO.Deployment deployment = webhook.getDeployment();
        
        logger.info("🚀 Processing GitHub deploy webhook");
        logger.info("   Repository: {}", webhook.getRepository().getName());
        logger.info("   Deployment ID: {}", deployment.getId());
        logger.info("   Environment: {}", deployment.getEnvironment());
        logger.info("   Status: {}", deployment.getStatus().getState());
        logger.info("   Commit: {}", deployment.getSha());
        
        // Simulate real service logic
        GitHubDeploy deploy = new GitHubDeploy();
        deploy.id = deployment.getId();
        deploy.repository = webhook.getRepository().getName();
        deploy.environment = deployment.getEnvironment();
        deploy.status = deployment.getStatus().getState();
        deploy.commit = deployment.getSha();
        deploy.startedAt = deployment.getCreated_at().atZone(ZoneId.systemDefault()).toLocalDateTime();
        deploy.version = deployment.getEnvironment(); // Using environment as version
        
        // Store in mock database
        deployStore.put(deployment.getId(), deploy);
        
        // Simulate processing delay
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        logger.info("✅ GitHub deploy processed successfully");
        logger.info("   Stored deploy: {} for {}", deployment.getId(), webhook.getRepository().getName());
    }
    
    public void processJenkinsDeploy(JenkinsDeployWebhookDTO webhook) {
        logger.info("Processing Jenkins deploy webhook: {}", webhook.getJob().getName());
        
        // Mock Jenkins processing
        String deployId = webhook.getJob().getName() + "-" + webhook.getBuild().getNumber();
        
        try {
            Thread.sleep(150);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        logger.info("Jenkins deploy processed successfully: {}", deployId);
    }
    
    public GitHubDeploy getRecentDeploy(String serviceName) {
        // Return most recent deploy for the service
        return deployStore.values().stream()
            .filter(d -> d.repository.equals(serviceName))
            .sorted((a, b) -> b.startedAt.compareTo(a.startedAt))
            .findFirst()
            .orElse(null);
    }
    
    public static class GitHubDeploy {
        public String id;
        public String repository;
        public String environment;
        public String status;
        public String commit;
        public String version;
        public LocalDateTime startedAt;
    }
}

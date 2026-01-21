package com.signalroot.service;

import com.signalroot.dto.GitHubDeployWebhookDTO;
import com.signalroot.dto.JenkinsDeployWebhookDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

@Service
public class MockDeployService {
    
    private static final Logger logger = LoggerFactory.getLogger(MockDeployService.class);
    private final Map<String, LocalDateTime> deployStore = new ConcurrentHashMap<>();
    
    public void processGitHubDeploy(GitHubDeployWebhookDTO webhook) {
        String deployId = webhook.getDeployment().getId();
        logger.info("Processing GitHub deploy: {} for {}", deployId, webhook.getRepository().getName());
        
        // Simulate deploy processing
        deployStore.put("github:" + deployId, LocalDateTime.now());
        
        // Simulate delay
        try {
            Thread.sleep(150);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        logger.info("GitHub deploy processed successfully: {}", deployId);
    }
    
    public void processJenkinsDeploy(JenkinsDeployWebhookDTO webhook) {
        String deployId = webhook.getJob().getName() + "-" + webhook.getBuild().getNumber();
        logger.info("Processing Jenkins deploy: {} for {}", deployId, webhook.getJob().getName());
        
        // Simulate deploy processing
        deployStore.put("jenkins:" + deployId, LocalDateTime.now());
        
        // Simulate delay
        try {
            Thread.sleep(150);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        logger.info("Jenkins deploy processed successfully: {}", deployId);
    }
    
    public boolean deployExists(String externalId, String source) {
        return deployStore.containsKey(source + ":" + externalId);
    }
}

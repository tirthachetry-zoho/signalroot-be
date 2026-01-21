package com.signalroot.service;

import com.signalroot.dto.PagerDutyWebhookDTO;
import com.signalroot.dto.CloudWatchWebhookDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

@Service
public class MockAlertService {
    
    private static final Logger logger = LoggerFactory.getLogger(MockAlertService.class);
    private final Map<String, LocalDateTime> alertStore = new ConcurrentHashMap<>();
    
    @Autowired
    private IdempotencyService idempotencyService;
    
    public void processPagerDutyAlert(PagerDutyWebhookDTO webhook) {
        String externalId = webhook.getPayload().getIncident().getId();
        String source = "pagerduty";
        
        // Idempotency check
        if (!idempotencyService.shouldProcessWebhook(webhook.getWebhookId(), source, externalId)) {
            return;
        }
        
        // Mark webhook as processed
        idempotencyService.markWebhookProcessed(webhook.getWebhookId(), source, externalId);
        
        // Check if incident should be created
        if (!idempotencyService.shouldCreateIncident(externalId, source)) {
            logger.info("Alert {} already has incident, skipping", externalId);
            return;
        }
        
        logger.info("Processing PagerDuty alert: {} - {}", externalId, webhook.getPayload().getIncident().getTitle());
        
        // Simulate alert processing
        alertStore.put(source + ":" + externalId, LocalDateTime.now());
        
        // Simulate delay
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        logger.info("PagerDuty alert processed successfully: {}", externalId);
    }
    
    public void processCloudWatchAlert(CloudWatchWebhookDTO webhook) {
        String externalId = webhook.getId();
        String source = "cloudwatch";
        
        // Idempotency check
        if (!idempotencyService.shouldProcessWebhook(webhook.getId(), source, externalId)) {
            return;
        }
        
        // Mark webhook as processed
        idempotencyService.markWebhookProcessed(webhook.getId(), source, externalId);
        
        // Check if incident should be created
        if (!idempotencyService.shouldCreateIncident(externalId, source)) {
            logger.info("Alert {} already has incident, skipping", externalId);
            return;
        }
        
        logger.info("Processing CloudWatch alert: {}", externalId);
        
        // Simulate alert processing
        alertStore.put(source + ":" + externalId, LocalDateTime.now());
        
        // Simulate delay
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        logger.info("CloudWatch alert processed successfully: {}", externalId);
    }
    
    public boolean alertExists(String externalId, String source) {
        return alertStore.containsKey(source + ":" + externalId);
    }
}

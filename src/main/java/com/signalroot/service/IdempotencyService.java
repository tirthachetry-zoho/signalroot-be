package com.signalroot.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class IdempotencyService {
    
    private static final Logger logger = LoggerFactory.getLogger(IdempotencyService.class);
    
    // Track processed webhooks to prevent duplicates
    private final Map<String, WebhookProcessing> processedWebhooks = new ConcurrentHashMap<>();
    
    // Track incidents to prevent multiple notifications
    private final Map<String, LocalDateTime> incidentNotifications = new ConcurrentHashMap<>();
    
    // Track alert processing to prevent duplicate incidents
    private final Map<String, String> alertToIncident = new ConcurrentHashMap<>();
    
    // Time windows for idempotency checks
    private static final long WEBHOOK_DEDUPE_WINDOW_MINUTES = 30;
    private static final long NOTIFICATION_DEDUPE_WINDOW_MINUTES = 60;
    
    /**
     * Check if webhook should be processed (idempotency check)
     */
    public boolean shouldProcessWebhook(String webhookId, String source, String externalId) {
        String key = source + ":" + externalId;
        
        WebhookProcessing existing = processedWebhooks.get(key);
        if (existing != null) {
            // Check if within dedupe window
            if (existing.processedAt.plusMinutes(WEBHOOK_DEDUPE_WINDOW_MINUTES).isAfter(LocalDateTime.now())) {
                // Window expired, allow processing
                processedWebhooks.remove(key);
                logger.info("Webhook dedupe window expired for: {}", key);
                return true;
            } else {
                // Still within window, skip processing
                logger.info("Skipping duplicate webhook: {} (processed at {})", key, existing.processedAt);
                return false;
            }
        }
        
        return true;
    }
    
    /**
     * Mark webhook as processed
     */
    public void markWebhookProcessed(String webhookId, String source, String externalId) {
        String key = source + ":" + externalId;
        WebhookProcessing processing = new WebhookProcessing();
        processing.webhookId = webhookId;
        processing.processedAt = LocalDateTime.now();
        processedWebhooks.put(key, processing);
        
        logger.info("Marked webhook as processed: {} at {}", key, processing.processedAt);
    }
    
    /**
     * Check if incident should be created for this alert
     */
    public boolean shouldCreateIncident(String alertId, String source) {
        String key = source + ":" + alertId;
        
        String existingIncidentId = alertToIncident.get(key);
        if (existingIncidentId != null) {
            logger.info("Alert {} already has incident: {}", alertId, existingIncidentId);
            return false;
        }
        
        return true;
    }
    
    /**
     * Associate alert with incident
     */
    public void associateAlertWithIncident(String alertId, String source, String incidentId) {
        String key = source + ":" + alertId;
        alertToIncident.put(key, incidentId);
        logger.info("Associated alert {} with incident {}", alertId, incidentId);
    }
    
    /**
     * Check if notification should be sent for incident
     */
    public boolean shouldSendNotification(String incidentId) {
        LocalDateTime lastNotification = incidentNotifications.get(incidentId);
        
        if (lastNotification == null) {
            return true;
        }
        
        // Check if notification window has expired
        if (lastNotification.plusMinutes(NOTIFICATION_DEDUPE_WINDOW_MINUTES).isAfter(LocalDateTime.now())) {
            incidentNotifications.remove(incidentId);
            logger.info("Notification window expired for incident: {}", incidentId);
            return true;
        }
        
        logger.info("Skipping duplicate notification for incident: {} (last sent at {})", 
                   incidentId, lastNotification);
        return false;
    }
    
    /**
     * Mark notification as sent for incident
     */
    public void markNotificationSent(String incidentId) {
        incidentNotifications.put(incidentId, LocalDateTime.now());
        logger.info("Marked notification sent for incident: {}", incidentId);
    }
    
    /**
     * Clean up old entries (call periodically)
     */
    public void cleanup() {
        LocalDateTime cutoff = LocalDateTime.now().minusHours(2);
        
        // Clean old webhook processing records
        processedWebhooks.entrySet().removeIf(entry -> 
            entry.getValue().processedAt.isBefore(cutoff));
        
        // Clean old notification records  
        incidentNotifications.entrySet().removeIf(entry -> 
            entry.getValue().isBefore(cutoff));
        
        if (!processedWebhooks.isEmpty() || !incidentNotifications.isEmpty()) {
            logger.info("Cleaned up {} webhook records and {} notification records", 
                       processedWebhooks.size(), incidentNotifications.size());
        }
    }
    
    /**
     * Get statistics for monitoring
     */
    public IdempotencyStats getStats() {
        IdempotencyStats stats = new IdempotencyStats();
        stats.processedWebhooks = processedWebhooks.size();
        stats.activeNotifications = incidentNotifications.size();
        stats.alertToIncidentMappings = alertToIncident.size();
        return stats;
    }
    
    public static class WebhookProcessing {
        public String webhookId;
        public LocalDateTime processedAt;
    }
    
    public static class IdempotencyStats {
        public int processedWebhooks;
        public int activeNotifications;
        public int alertToIncidentMappings;
    }
}

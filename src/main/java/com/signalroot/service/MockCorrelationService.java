package com.signalroot.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

@Service
public class MockCorrelationService {
    
    private static final Logger logger = LoggerFactory.getLogger(MockCorrelationService.class);
    private final Map<String, MockIncident> incidentStore = new ConcurrentHashMap<>();
    
    @Autowired
    private MockNotificationService notificationService;
    
    @Autowired
    private IdempotencyService idempotencyService;
    
    public void processAlert(String externalAlertId, String source) {
        logger.info("Processing correlation for alert: {} from {}", externalAlertId, source);
        
        // Check if incident should be created for this alert
        if (!idempotencyService.shouldCreateIncident(externalAlertId, source)) {
            logger.info("Alert {} already has incident, skipping correlation", externalAlertId);
            return;
        }
        
        // Create mock incident
        MockIncident incident = new MockIncident();
        incident.setId(UUID.randomUUID().toString());
        incident.setTitle("Mock Alert: " + externalAlertId);
        incident.setService("mock-service");
        incident.setSeverity("HIGH");
        incident.setStatus("ACTIVE");
        incident.setStartedAt(LocalDateTime.now());
        
        // Simulate correlation logic
        incident.setRecentDeploy("GitHub deploy #123 (15 mins ago) - payment-service v2.4.1");
        incident.setSimilarIncident("Similar incident 2 days ago - Same service, same error pattern");
        incident.setSuggestedChecks("1. Check payment-service health endpoints\n2. Review recent error logs\n3. Verify database connection\n4. Check Redis cache status");
        
        // Associate alert with incident
        idempotencyService.associateAlertWithIncident(externalAlertId, source, incident.getId());
        
        incidentStore.put(incident.getId(), incident);
        
        // Send notification (with idempotency check)
        try {
            if (idempotencyService.shouldSendNotification(incident.getId())) {
                notificationService.sendEnrichedNotification(incident);
                idempotencyService.markNotificationSent(incident.getId());
            } else {
                logger.info("Skipping duplicate notification for incident: {}", incident.getId());
            }
        } catch (Exception e) {
            logger.error("Failed to send notification", e);
        }
        
        logger.info("Correlation completed for incident: {}", incident.getId());
    }
    
    public MockIncident getIncident(String id) {
        return incidentStore.get(id);
    }
    
    public static class MockIncident {
        private String id;
        private String title;
        private String service;
        private String severity;
        private String status;
        private LocalDateTime startedAt;
        private String recentDeploy;
        private String similarIncident;
        private String suggestedChecks;
        
        // Getters and setters
        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public String getService() { return service; }
        public void setService(String service) { this.service = service; }
        public String getSeverity() { return severity; }
        public void setSeverity(String severity) { this.severity = severity; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public LocalDateTime getStartedAt() { return startedAt; }
        public void setStartedAt(LocalDateTime startedAt) { this.startedAt = startedAt; }
        public String getRecentDeploy() { return recentDeploy; }
        public void setRecentDeploy(String recentDeploy) { this.recentDeploy = recentDeploy; }
        public String getSimilarIncident() { return similarIncident; }
        public void setSimilarIncident(String similarIncident) { this.similarIncident = similarIncident; }
        public String getSuggestedChecks() { return suggestedChecks; }
        public void setSuggestedChecks(String suggestedChecks) { this.suggestedChecks = suggestedChecks; }
    }
}

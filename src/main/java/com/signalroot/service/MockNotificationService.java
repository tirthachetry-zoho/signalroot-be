package com.signalroot.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class MockNotificationService {
    
    private static final Logger logger = LoggerFactory.getLogger(MockNotificationService.class);
    
    public void sendEnrichedNotification(Object incident) {
        if (incident instanceof MockCorrelationService.MockIncident) {
            MockCorrelationService.MockIncident mockIncident = (MockCorrelationService.MockIncident) incident;
            
            // Send Slack notification
            sendSlackNotification(mockIncident);
            
            // Send email notification
            sendEmailNotification(mockIncident);
        }
    }
    
    private void sendSlackNotification(MockCorrelationService.MockIncident incident) {
        logger.info("📱 SLACK NOTIFICATION SENT:");
        logger.info("🚨 *{}* - {} | {}", 
                   getSeverityEmoji(incident.getSeverity()), 
                   incident.getService(), 
                   incident.getTitle());
        logger.info("");
        logger.info("🔍 **What to check first:**");
        logger.info("   {}", incident.getSuggestedChecks());
        logger.info("");
        logger.info("📦 **Recent Deploy:**");
        logger.info("   {}", incident.getRecentDeploy());
        logger.info("");
        logger.info("🔗 **View Details:** http://localhost:3000/incidents/{}", incident.getId());
        logger.info("");
        logger.info("📋 **Similar Issue:**");
        logger.info("   {}", incident.getSimilarIncident());
    }
    
    private void sendEmailNotification(MockCorrelationService.MockIncident incident) {
        logger.info("📧 EMAIL NOTIFICATION SENT:");
        logger.info("   To: oncall@company.com");
        logger.info("   Subject: [{}] {} - {}", 
                   incident.getSeverity(), 
                   incident.getService(), 
                   incident.getTitle());
        logger.info("   Incident ID: {}", incident.getId());
    }
    
    private String getSeverityEmoji(String severity) {
        switch (severity.toUpperCase()) {
            case "CRITICAL": return "🔴";
            case "HIGH": return "🟠";
            case "MEDIUM": return "🟡";
            case "LOW": return "🟢";
            default: return "⚪";
        }
    }
}

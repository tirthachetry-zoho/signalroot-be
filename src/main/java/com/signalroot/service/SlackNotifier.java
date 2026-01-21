package com.signalroot.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
@Transactional
public class SlackNotifier {
    
    private static final Logger logger = LoggerFactory.getLogger(SlackNotifier.class);
    
    @Autowired
    private RestTemplate restTemplate;
    
    @Value("${signalroot.slack.webhook-url:}")
    private String webhookUrl;
    
    @Value("${signalroot.slack.channel:#alerts}")
    private String channel;
    
    @Value("${signalroot.slack.username:SignalRoot}")
    private String username;
    
    public void sendIncidentNotification(Object incident) {
        logger.info("Sending Slack notification for incident: {}", incident);
        
        try {
            Map<String, Object> payload = buildSlackPayload(incident);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(payload, headers);
            ResponseEntity<String> response = restTemplate.postForEntity(webhookUrl, request, String.class);
            
            if (response.getStatusCode().is2xxSuccessful()) {
                logger.info("Slack notification sent successfully for incident: {}", incident);
            } else {
                logger.error("Failed to send Slack notification. Status: {}, Response: {}", 
                           response.getStatusCode(), response.getBody());
            }
        } catch (Exception e) {
            logger.error("Error sending Slack notification for incident: {}", incident, e);
        }
    }
    
    private Map<String, Object> buildSlackPayload(Object incident) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("channel", channel);
        payload.put("username", username);
        payload.put("icon_emoji", ":warning:");
        
        StringBuilder message = new StringBuilder();
        message.append("*").append("Title").append("*\n\n");
        
        // Alert details
        message.append("*Alert Details:*\n");
        message.append("• *Service*: Service\n");
        message.append("• *Severity*: :warning: Severity\n");
        message.append("• *Source*: Source\n");
        message.append("• *Started*: Started\n");
        
        payload.put("text", message.toString());
        
        // Add color based on severity
        payload.put("attachments", new Object[]{
            Map.of("color", "#ff6600", "footer", "SignalRoot", "ts", System.currentTimeMillis() / 1000)
        });
        
        return payload;
    }
}

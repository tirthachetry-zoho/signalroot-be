package com.signalroot.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@ConditionalOnProperty(name = "signalroot.notifications.email.enabled", havingValue = "true")
public class EmailNotifier {
    
    private static final Logger logger = LoggerFactory.getLogger(EmailNotifier.class);
    
    @Autowired
    private JavaMailSender mailSender;
    
    @Value("${signalroot.notifications.email.from:alerts@signalroot.com}")
    private String fromEmail;
    
    @Value("${signalroot.notifications.email.to:}")
    private String toEmail;
    
    public void sendIncidentNotification(Object incident) {
        logger.info("Sending email notification for incident: {}", incident);
        
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject(buildSubject(incident));
            message.setText(buildEmailBody(incident));
            
            mailSender.send(message);
            logger.info("Email notification sent successfully for incident: {}", incident);
        } catch (Exception e) {
            logger.error("Failed to send email notification for incident: {}", incident, e);
        }
    }
    
    private String buildSubject(Object incident) {
        StringBuilder subject = new StringBuilder();
        subject.append("[").append("Severity").append("] ");
        subject.append("Title").append(" - ").append("Service");
        return subject.toString();
    }
    
    private String buildEmailBody(Object incident) {
        StringBuilder body = new StringBuilder();
        
        body.append("SignalRoot Alert Notification\n");
        body.append("=============================\n\n");
        
        body.append("Incident: Title\n");
        body.append("Service: Service\n");
        body.append("Severity: Severity\n");
        body.append("Status: Status\n");
        body.append("Started: Started\n");
        
        body.append("\n--\nSignalRoot\nAlert Enrichment Service");
        return body.toString();
    }
}

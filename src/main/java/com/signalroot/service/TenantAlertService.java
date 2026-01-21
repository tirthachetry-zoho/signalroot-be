package com.signalroot.service;

import com.signalroot.entity.Alert;
import com.signalroot.entity.Organization;
import com.signalroot.repository.AlertRepository;
import com.signalroot.repository.ServiceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;

@Service
@ConditionalOnProperty(name = "signalroot.service.mode", havingValue = "real")
public class TenantAlertService implements TenantAlertServiceInterface {
    
    private static final Logger logger = LoggerFactory.getLogger(TenantAlertService.class);
    
    @Autowired
    private AlertRepository alertRepository;
    
    @Autowired
    private ServiceRepository serviceRepository;
    
    @Autowired
    private IdempotencyService idempotencyService;
    
    public void processPagerDutyAlert(Map<String, Object> payload, Organization organization) {
        logger.info("🚨 Processing PagerDuty alert for organization: {}", organization.getName());
        
        try {
            Map<String, Object> incident = (Map<String, Object>) payload.get("incident");
            String externalId = (String) incident.get("id");
            String source = "pagerduty";
            
            // Idempotency check
            if (!idempotencyService.shouldProcessWebhook((String) payload.get("webhookId"), source, externalId)) {
                return;
            }
            
            // Mark webhook as processed
            idempotencyService.markWebhookProcessed((String) payload.get("webhookId"), source, externalId);
            
            // Check if incident should be created
            if (!idempotencyService.shouldCreateIncident(externalId, source)) {
                logger.info("Alert {} already has incident, skipping", externalId);
                return;
            }
            
            // Find or create service
            Map<String, Object> serviceData = (Map<String, Object>) incident.get("service");
            String serviceName = (String) serviceData.get("name");
            com.signalroot.entity.Service service = findOrCreateService(serviceName, organization);
            
            // Create alert
            Alert alert = new Alert();
            alert.setExternalId(externalId);
            alert.setSource(source);
            alert.setService(service);
            alert.setOrganization(organization);
            alert.setSeverity(mapPagerDutySeverity((String) incident.get("severity")));
            alert.setTitle((String) incident.get("title"));
            alert.setStatus(mapPagerDutyStatus((String) incident.get("status")));
            
            alertRepository.save(alert);
            
            // Associate alert with incident
            idempotencyService.associateAlertWithIncident(externalId, source, alert.getId().toString());
            
            logger.info("✅ PagerDuty alert processed: {} for organization: {}", externalId, organization.getName());
            
        } catch (Exception e) {
            logger.error("❌ Failed to process PagerDuty alert for organization: {}", organization.getName(), e);
            throw e;
        }
    }
    
    public void processCloudWatchAlert(Map<String, Object> payload, Organization organization) {
        logger.info("☁️ Processing CloudWatch alert for organization: {}", organization.getName());
        
        try {
            String externalId = (String) payload.get("id");
            String source = "cloudwatch";
            
            // Idempotency check
            if (!idempotencyService.shouldProcessWebhook(externalId, source, externalId)) {
                return;
            }
            
            // Mark webhook as processed
            idempotencyService.markWebhookProcessed(externalId, source, externalId);
            
            // Check if incident should be created
            if (!idempotencyService.shouldCreateIncident(externalId, source)) {
                logger.info("Alert {} already has incident, skipping", externalId);
                return;
            }
            
            // Extract service name from alarm name
            Map<String, Object> detail = (Map<String, Object>) payload.get("detail");
            String alarmName = (String) detail.get("alarmName");
            String serviceName = extractServiceNameFromAlarm(alarmName);
            
            com.signalroot.entity.Service service = findOrCreateService(serviceName, organization);
            
            // Create alert
            Alert alert = new Alert();
            alert.setExternalId(externalId);
            alert.setSource(source);
            alert.setService(service);
            alert.setOrganization(organization);
            alert.setSeverity(mapCloudWatchSeverity((String) detail.get("state")));
            alert.setTitle(alarmName);
            alert.setStatus(Alert.AlertStatus.FIRING);
            
            alertRepository.save(alert);
            
            // Associate alert with incident
            idempotencyService.associateAlertWithIncident(externalId, source, alert.getId().toString());
            
            logger.info("✅ CloudWatch alert processed: {} for organization: {}", externalId, organization.getName());
            
        } catch (Exception e) {
            logger.error("❌ Failed to process CloudWatch alert for organization: {}", organization.getName(), e);
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
    
    private Alert.AlertSeverity mapPagerDutySeverity(String severity) {
        return switch (severity.toLowerCase()) {
            case "critical" -> Alert.AlertSeverity.CRITICAL;
            case "high" -> Alert.AlertSeverity.HIGH;
            case "warning" -> Alert.AlertSeverity.MEDIUM;
            case "info" -> Alert.AlertSeverity.LOW;
            default -> Alert.AlertSeverity.MEDIUM;
        };
    }
    
    private Alert.AlertStatus mapPagerDutyStatus(String status) {
        return switch (status.toLowerCase()) {
            case "triggered" -> Alert.AlertStatus.FIRING;
            case "resolved" -> Alert.AlertStatus.RESOLVED;
            case "acknowledged" -> Alert.AlertStatus.FIRING;
            default -> Alert.AlertStatus.FIRING;
        };
    }
    
    private Alert.AlertSeverity mapCloudWatchSeverity(String state) {
        return switch (state.toUpperCase()) {
            case "ALARM" -> Alert.AlertSeverity.HIGH;
            case "OK" -> Alert.AlertSeverity.LOW;
            case "INSUFFICIENT_DATA" -> Alert.AlertSeverity.MEDIUM;
            default -> Alert.AlertSeverity.MEDIUM;
        };
    }
    
    private String extractServiceNameFromAlarm(String alarmName) {
        // Extract service name from alarm name like "PaymentService-HighCPU"
        if (alarmName.contains("-")) {
            return alarmName.split("-")[0].toLowerCase();
        }
        return alarmName.toLowerCase();
    }
}

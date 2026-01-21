package com.signalroot.service;

import com.signalroot.config.ApplicationConfig;
import com.signalroot.entity.Organization;
import com.signalroot.service.TenantAlertServiceInterface;
import com.signalroot.service.TenantDeployServiceInterface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class TestEventService {
    
    private static final Logger logger = LoggerFactory.getLogger(TestEventService.class);
    
    @Autowired
    private TenantAlertServiceInterface alertService;
    
    @Autowired
    private TenantDeployServiceInterface deployService;
    
    public Map<String, Object> sendTestPagerDutyAlert(Organization organization) {
        logger.info("🧪 Sending test PagerDuty alert for organization: {}", organization.getName());
        
        // Create test PagerDuty payload
        Map<String, Object> payload = createTestPagerDutyPayload();
        
        try {
            alertService.processPagerDutyAlert(payload, organization);
            
            Map<String, Object> result = new HashMap<>();
            result.put("status", "success");
            result.put("message", "Test PagerDuty alert sent successfully");
            result.put("organization", organization.getName());
            
            return result;
        } catch (Exception e) {
            logger.error("❌ Failed to send test PagerDuty alert for organization: {}", organization.getName(), e);
            
            Map<String, Object> result = new HashMap<>();
            result.put("status", "error");
            result.put("message", "Failed to send test PagerDuty alert: " + e.getMessage());
            result.put("organization", organization.getName());
            
            return result;
        }
    }
    
    public Map<String, Object> sendTestCloudWatchAlert(Organization organization) {
        logger.info("☁️ Sending test CloudWatch alert for organization: {}", organization.getName());
        
        // Create test CloudWatch payload
        Map<String, Object> payload = createTestCloudWatchPayload();
        
        try {
            alertService.processCloudWatchAlert(payload, organization);
            
            Map<String, Object> result = new HashMap<>();
            result.put("status", "success");
            result.put("message", "Test CloudWatch alert sent successfully");
            result.put("alarmName", ((Map<String, Object>) payload.get("detail")).get("alarmName"));
            result.put("organization", organization.getName());
            
            return result;
        } catch (Exception e) {
            logger.error("❌ Failed to send test CloudWatch alert for organization: {}", organization.getName(), e);
            
            Map<String, Object> result = new HashMap<>();
            result.put("status", "error");
            result.put("message", "Failed to send test CloudWatch alert: " + e.getMessage());
            result.put("organization", organization.getName());
            
            return result;
        }
    }
    
    public Map<String, Object> sendTestGitHubDeploy(Organization organization) {
        logger.info("🐙 Sending test GitHub deploy for organization: {}", organization.getName());
        
        // Create test GitHub payload
        Map<String, Object> payload = createTestGitHubPayload();
        
        try {
            deployService.processGitHubDeploy(payload, organization);
            
            Map<String, Object> result = new HashMap<>();
            result.put("status", "success");
            result.put("message", "Test GitHub deploy sent successfully");
            result.put("repository", ((Map<String, Object>) payload.get("repository")).get("full_name"));
            result.put("organization", organization.getName());
            
            return result;
        } catch (Exception e) {
            logger.error("❌ Failed to send test GitHub deploy for organization: {}", organization.getName(), e);
            
            Map<String, Object> result = new HashMap<>();
            result.put("status", "error");
            result.put("message", "Failed to send test GitHub deploy: " + e.getMessage());
            result.put("organization", organization.getName());
            
            return result;
        }
    }
    
    public Map<String, Object> sendTestJenkinsDeploy(Organization organization) {
        logger.info("🔧 Sending test Jenkins deploy for organization: {}", organization.getName());
        
        // Create test Jenkins payload
        Map<String, Object> payload = createTestJenkinsPayload();
        
        try {
            deployService.processJenkinsDeploy(payload, organization);
            
            Map<String, Object> result = new HashMap<>();
            result.put("status", "success");
            result.put("message", "Test Jenkins deploy sent successfully");
            result.put("jobName", ((Map<String, Object>) payload.get("job")).get("name"));
            result.put("organization", organization.getName());
            
            return result;
        } catch (Exception e) {
            logger.error("❌ Failed to send test Jenkins deploy for organization: {}", organization.getName(), e);
            
            Map<String, Object> result = new HashMap<>();
            result.put("status", "error");
            result.put("message", "Failed to send test Jenkins deploy: " + e.getMessage());
            result.put("organization", organization.getName());
            
            return result;
        }
    }
    
    private Map<String, Object> createTestPagerDutyPayload() {
        Map<String, Object> payload = new HashMap<>();
        
        Map<String, Object> incident = new HashMap<>();
        incident.put("id", UUID.randomUUID().toString());
        incident.put("title", "Test Alert: High CPU on payment-service");
        incident.put("severity", "high");
        incident.put("status", "triggered");
        incident.put("service", Map.of(
            "id", UUID.randomUUID().toString(),
            "name", "payment-service"
        ));
        
        payload.put("webhookId", "test-webhook-" + UUID.randomUUID().toString());
        payload.put("incident", incident);
        
        return payload;
    }
    
    private Map<String, Object> createTestCloudWatchPayload() {
        Map<String, Object> payload = new HashMap<>();
        
        Map<String, Object> detail = new HashMap<>();
        detail.put("alarmName", "payment-service-high-cpu");
        detail.put("state", "ALARM");
        detail.put("metric", "CPUUtilization");
        detail.put("threshold", "80");
        detail.put("value", "85.5");
        
        payload.put("id", "ALARM-" + UUID.randomUUID().toString());
        payload.put("detail", detail);
        
        return payload;
    }
    
    private Map<String, Object> createTestGitHubPayload() {
        Map<String, Object> payload = new HashMap<>();
        
        Map<String, Object> deployment = new HashMap<>();
        deployment.put("id", UUID.randomUUID().toString());
        deployment.put("environment", "production");
        deployment.put("sha", "abc123def456");
        
        Map<String, Object> repository = new HashMap<>();
        repository.put("full_name", "signalroot/test-repo");
        repository.put("name", "test-repo");
        
        Map<String, Object> status = new HashMap<>();
        status.put("state", "success");
        
        payload.put("repository", repository);
        payload.put("deployment", deployment);
        payload.put("id", "DEPLOY-" + UUID.randomUUID().toString());
        
        return payload;
    }
    
    private Map<String, Object> createTestJenkinsPayload() {
        Map<String, Object> payload = new HashMap<>();
        
        Map<String, Object> build = new HashMap<>();
        build.put("number", "123");
        build.put("timestamp", "2026-01-20T17:02:00Z");
        build.put("status", "SUCCESS");
        
        Map<String, Object> artifact = new HashMap<>();
        artifact.put("version", "1.0.0");
        
        Map<String, Object> job = new HashMap<>();
        job.put("name", "payment-service-deploy");
        
        Map<String, Object> resultPayload = new HashMap<>();
        resultPayload.put("build", build);
        resultPayload.put("artifact", artifact);
        resultPayload.put("job", job);
        resultPayload.put("id", "BUILD-" + UUID.randomUUID().toString());
        
        return resultPayload;
    }
}

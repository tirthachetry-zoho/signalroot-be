package com.signalroot.controller;

import com.signalroot.entity.Organization;
import com.signalroot.service.OrganizationServiceInterface;
import com.signalroot.service.TenantAlertServiceInterface;
import com.signalroot.service.TenantDeployServiceInterface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/inbound")
public class InboundWebhookController {
    
    private static final Logger logger = LoggerFactory.getLogger(InboundWebhookController.class);
    
    @Autowired
    private OrganizationServiceInterface organizationService;
    
    @Autowired
    private TenantAlertServiceInterface alertService;
    
    @Autowired
    private TenantDeployServiceInterface deployService;
    
    // PagerDuty webhook endpoint
    @PostMapping("/pagerduty/{orgKey}")
    public ResponseEntity<Map<String, String>> handlePagerDutyWebhook(
            @PathVariable String orgKey,
            @RequestBody Map<String, Object> payload,
            @RequestHeader(value = "X-PagerDuty-Webhook-Signature", required = false) String signature) {
        
        logger.info("🚨 PagerDuty webhook received for organization: {}", orgKey);
        
        try {
            Organization org = organizationService.findByOrganizationKey(orgKey);
            if (org == null) {
                logger.warn("Organization not found: {}", orgKey);
                return ResponseEntity.notFound().build();
            }
            
            // Process PagerDuty alert
            alertService.processPagerDutyAlert(payload, org);
            
            Map<String, String> response = new HashMap<>();
            response.put("status", "processed");
            response.put("organization", org.getName());
            response.put("message", "PagerDuty alert processed successfully");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Failed to process PagerDuty webhook for org: {}", orgKey, e);
            
            Map<String, String> error = new HashMap<>();
            error.put("status", "error");
            error.put("message", "Failed to process PagerDuty alert");
            
            return ResponseEntity.status(500).body(error);
        }
    }
    
    // CloudWatch webhook endpoint
    @PostMapping("/cloudwatch/{orgKey}")
    public ResponseEntity<Map<String, String>> handleCloudWatchWebhook(
            @PathVariable String orgKey,
            @RequestBody Map<String, Object> payload) {
        
        logger.info("☁️ CloudWatch webhook received for organization: {}", orgKey);
        
        try {
            Organization org = organizationService.findByOrganizationKey(orgKey);
            if (org == null) {
                logger.warn("Organization not found: {}", orgKey);
                return ResponseEntity.notFound().build();
            }
            
            // Process CloudWatch alert
            alertService.processCloudWatchAlert(payload, org);
            
            Map<String, String> response = new HashMap<>();
            response.put("status", "processed");
            response.put("organization", org.getName());
            response.put("message", "CloudWatch alert processed successfully");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Failed to process CloudWatch webhook for org: {}", orgKey, e);
            
            Map<String, String> error = new HashMap<>();
            error.put("status", "error");
            error.put("message", "Failed to process CloudWatch alert");
            
            return ResponseEntity.status(500).body(error);
        }
    }
    
    // GitHub Actions webhook endpoint
    @PostMapping("/github/{orgKey}")
    public ResponseEntity<Map<String, String>> handleGitHubWebhook(
            @PathVariable String orgKey,
            @RequestBody Map<String, Object> payload,
            @RequestHeader(value = "X-Hub-Signature-256", required = false) String signature) {
        
        logger.info("🐙 GitHub webhook received for organization: {}", orgKey);
        
        try {
            Organization org = organizationService.findByOrganizationKey(orgKey);
            if (org == null) {
                logger.warn("Organization not found: {}", orgKey);
                return ResponseEntity.notFound().build();
            }
            
            // Process GitHub deploy event
            deployService.processGitHubDeploy(payload, org);
            
            Map<String, String> response = new HashMap<>();
            response.put("status", "processed");
            response.put("organization", org.getName());
            response.put("message", "GitHub deploy event processed successfully");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Failed to process GitHub webhook for org: {}", orgKey, e);
            
            Map<String, String> error = new HashMap<>();
            error.put("status", "error");
            error.put("message", "Failed to process GitHub deploy event");
            
            return ResponseEntity.status(500).body(error);
        }
    }
    
    // Jenkins webhook endpoint
    @PostMapping("/jenkins/{orgKey}")
    public ResponseEntity<Map<String, String>> handleJenkinsWebhook(
            @PathVariable String orgKey,
            @RequestBody Map<String, Object> payload) {
        
        logger.info("🔧 Jenkins webhook received for organization: {}", orgKey);
        
        try {
            Organization org = organizationService.findByOrganizationKey(orgKey);
            if (org == null) {
                logger.warn("Organization not found: {}", orgKey);
                return ResponseEntity.notFound().build();
            }
            
            // Process Jenkins deploy event
            deployService.processJenkinsDeploy(payload, org);
            
            Map<String, String> response = new HashMap<>();
            response.put("status", "processed");
            response.put("organization", org.getName());
            response.put("message", "Jenkins deploy event processed successfully");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Failed to process Jenkins webhook for org: {}", orgKey, e);
            
            Map<String, String> error = new HashMap<>();
            error.put("status", "error");
            error.put("message", "Failed to process Jenkins deploy event");
            
            return ResponseEntity.status(500).body(error);
        }
    }
}

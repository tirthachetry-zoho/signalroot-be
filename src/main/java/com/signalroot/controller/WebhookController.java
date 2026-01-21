package com.signalroot.controller;

import com.signalroot.dto.PagerDutyWebhookDTO;
import com.signalroot.dto.CloudWatchWebhookDTO;
import com.signalroot.dto.GitHubDeployWebhookDTO;
import com.signalroot.dto.JenkinsDeployWebhookDTO;
import com.signalroot.service.MockAlertService;
import com.signalroot.service.MockGitHubService;
import com.signalroot.service.MockCorrelationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/webhooks")
@Tag(name = "webhooks", description = "Webhook processing and management")
public class WebhookController {
    
    private static final Logger logger = LoggerFactory.getLogger(WebhookController.class);
    
    @Autowired
    private MockAlertService alertService;
    
    @Autowired
    private MockGitHubService deployService;
    
    @Autowired
    private MockCorrelationService correlationService;
    
    @PostMapping("/alerts/pagerduty")
    public ResponseEntity<String> handlePagerDutyAlert(@RequestBody PagerDutyWebhookDTO webhook) {
        logger.info("Received PagerDuty webhook: {}", webhook.getWebhookId());
        
        try {
            alertService.processPagerDutyAlert(webhook);
            correlationService.processAlert(webhook.getPayload().getIncident().getId(), "pagerduty");
            return ResponseEntity.ok("Alert processed successfully");
        } catch (Exception e) {
            logger.error("Error processing PagerDuty alert", e);
            return ResponseEntity.internalServerError().body("Error processing alert");
        }
    }
    
    @PostMapping("/alerts/cloudwatch")
    public ResponseEntity<String> handleCloudWatchAlert(@RequestBody CloudWatchWebhookDTO webhook) {
        logger.info("Received CloudWatch webhook: {}", webhook.getId());
        
        try {
            alertService.processCloudWatchAlert(webhook);
            correlationService.processAlert(webhook.getId(), "cloudwatch");
            return ResponseEntity.ok("Alert processed successfully");
        } catch (Exception e) {
            logger.error("Error processing CloudWatch alert", e);
            return ResponseEntity.internalServerError().body("Error processing alert");
        }
    }
    
    @PostMapping("/deploy/github")
    public ResponseEntity<String> handleGitHubDeploy(@RequestBody GitHubDeployWebhookDTO webhook) {
        logger.info("Received GitHub deploy webhook for: {}", webhook.getRepository().getName());
        
        try {
            deployService.processGitHubDeploy(webhook);
            return ResponseEntity.ok("Deploy event processed successfully");
        } catch (Exception e) {
            logger.error("Error processing GitHub deploy", e);
            return ResponseEntity.internalServerError().body("Error processing deploy");
        }
    }
    
    @PostMapping("/deploy/jenkins")
    public ResponseEntity<String> handleJenkinsDeploy(@RequestBody JenkinsDeployWebhookDTO webhook) {
        logger.info("Received Jenkins deploy webhook for: {}", webhook.getJob().getName());
        
        try {
            deployService.processJenkinsDeploy(webhook);
            return ResponseEntity.ok("Deploy event processed successfully");
        } catch (Exception e) {
            logger.error("Error processing Jenkins deploy", e);
            return ResponseEntity.internalServerError().body("Error processing deploy");
        }
    }
}

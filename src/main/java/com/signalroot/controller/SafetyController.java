package com.signalroot.controller;

import com.signalroot.service.IdempotencyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/safety")
@Tag(name = "safety", description = "Safety and idempotency features")
public class SafetyController {
    
    private static final Logger logger = LoggerFactory.getLogger(SafetyController.class);
    
    @Autowired
    private IdempotencyService idempotencyService;
    
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getSafetyStats() {
        Map<String, Object> stats = new HashMap<>();
        
        IdempotencyService.IdempotencyStats idempotencyStats = idempotencyService.getStats();
        
        stats.put("idempotency", Map.of(
            "processedWebhooks", idempotencyStats.processedWebhooks,
            "activeNotifications", idempotencyStats.activeNotifications,
            "alertToIncidentMappings", idempotencyStats.alertToIncidentMappings
        ));
        
        stats.put("status", "healthy");
        stats.put("timestamp", System.currentTimeMillis());
        
        return ResponseEntity.ok(stats);
    }
    
    @PostMapping("/cleanup")
    public ResponseEntity<String> cleanup() {
        logger.info("Manual cleanup triggered");
        
        idempotencyService.cleanup();
        
        IdempotencyService.IdempotencyStats stats = idempotencyService.getStats();
        
        return ResponseEntity.ok("Cleanup completed. Processed " + 
                             stats.processedWebhooks + " webhook records and " + 
                             stats.activeNotifications + " notification records");
    }
}

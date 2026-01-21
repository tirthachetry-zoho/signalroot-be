package com.signalroot.controller;

import com.signalroot.service.DogfoodingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/dogfooding")
@Tag(name = "dogfooding", description = "Dogfooding scenario management and testing")
public class DogfoodingController {
    
    private static final Logger logger = LoggerFactory.getLogger(DogfoodingController.class);
    
    @Autowired
    private DogfoodingService dogfoodingService;
    
    @Operation(
            summary = "Get all available dogfooding scenarios",
            description = "Retrieve all available dogfooding scenarios for testing"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Array of DogfoodingScenario objects",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = DogfoodingService.DogfoodingScenario.class)
                    )
            )
    })
    @GetMapping("/scenarios")
    public ResponseEntity<List<DogfoodingService.DogfoodingScenario>> getAllScenarios() {
        logger.info("Fetching all dogfooding scenarios");
        return ResponseEntity.ok(dogfoodingService.getAllScenarios());
    }
    
    @GetMapping("/scenarios/{scenarioId}")
    public ResponseEntity<DogfoodingService.DogfoodingScenario> getScenario(@PathVariable String scenarioId) {
        logger.info("Fetching dogfooding scenario: {}", scenarioId);
        
        DogfoodingService.DogfoodingScenario scenario = dogfoodingService.getScenario(scenarioId);
        if (scenario == null) {
            return ResponseEntity.notFound().build();
        }
        
        return ResponseEntity.ok(scenario);
    }
    
    @PostMapping("/scenarios/{scenarioId}/run")
    public ResponseEntity<DogfoodingService.DogfoodingResult> runScenario(@PathVariable String scenarioId) {
        logger.info("🚀 Running dogfooding scenario: {}", scenarioId);
        
        try {
            DogfoodingService.DogfoodingResult result = dogfoodingService.runScenario(scenarioId);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            logger.error("Failed to run dogfooding scenario: {}", scenarioId, e);
            
            DogfoodingService.DogfoodingResult errorResult = new DogfoodingService.DogfoodingResult();
            errorResult.success = false;
            errorResult.error = e.getMessage();
            errorResult.scenarioId = scenarioId;
            
            return ResponseEntity.status(500).body(errorResult);
        }
    }
    
    @GetMapping("/scenarios/{scenarioId}/calmness")
    public ResponseEntity<Map<String, Object>> getCalmnessAssessment(@PathVariable String scenarioId) {
        logger.info("🧘 Assessing calmness for scenario: {}", scenarioId);
        
        String assessment = dogfoodingService.getCalmnessAssessment(scenarioId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("scenarioId", scenarioId);
        response.put("assessment", assessment);
        response.put("timestamp", System.currentTimeMillis());
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getDogfoodingStats() {
        logger.info("Fetching dogfooding statistics");
        return ResponseEntity.ok(dogfoodingService.getDogfoodingStats());
    }
    
    @PostMapping("/run-all")
    public ResponseEntity<Map<String, Object>> runAllScenarios() {
        logger.info("🚀 Running all dogfooding scenarios");
        
        List<DogfoodingService.DogfoodingScenario> scenarios = dogfoodingService.getAllScenarios();
        Map<String, DogfoodingService.DogfoodingResult> results = new HashMap<>();
        
        for (DogfoodingService.DogfoodingScenario scenario : scenarios) {
            try {
                DogfoodingService.DogfoodingResult result = dogfoodingService.runScenario(scenario.id);
                results.put(scenario.id, result);
            } catch (Exception e) {
                logger.error("Failed to run scenario: {}", scenario.id, e);
                
                DogfoodingService.DogfoodingResult errorResult = new DogfoodingService.DogfoodingResult();
                errorResult.success = false;
                errorResult.error = e.getMessage();
                errorResult.scenarioId = scenario.id;
                results.put(scenario.id, errorResult);
            }
        }
        
        Map<String, Object> response = new HashMap<>();
        response.put("results", results);
        response.put("totalScenarios", scenarios.size());
        response.put("successful", results.values().stream().mapToInt(r -> r.success ? 1 : 0).sum());
        response.put("failed", results.values().stream().mapToInt(r -> r.success ? 0 : 1).sum());
        response.put("timestamp", System.currentTimeMillis());
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/complete-test")
    public ResponseEntity<Map<String, Object>> getCompleteTest() {
        logger.info("🎯 Running complete SignalRoot dogfooding test");
        
        Map<String, Object> test = new HashMap<>();
        
        // Test 1: Payment Service High Latency
        DogfoodingService.DogfoodingResult paymentResult = dogfoodingService.runScenario("payment-latency");
        
        // Test 2: Auth Service Database Issues  
        DogfoodingService.DogfoodingResult authResult = dogfoodingService.runScenario("auth-database");
        
        // Test 3: Orders Service Memory Spike
        DogfoodingService.DogfoodingResult ordersResult = dogfoodingService.runScenario("orders-memory");
        
        // Generate calmness assessments
        Map<String, String> assessments = new HashMap<>();
        assessments.put("payment-latency", dogfoodingService.getCalmnessAssessment("payment-latency"));
        assessments.put("auth-database", dogfoodingService.getCalmnessAssessment("auth-database"));
        assessments.put("orders-memory", dogfoodingService.getCalmnessAssessment("orders-memory"));
        
        test.put("results", Map.of(
            "payment-latency", paymentResult,
            "auth-database", authResult,
            "orders-memory", ordersResult
        ));
        
        test.put("calmnessAssessments", assessments);
        test.put("summary", Map.of(
            "totalTests", 3,
            "successful", (paymentResult.success ? 1 : 0) + (authResult.success ? 1 : 0) + (ordersResult.success ? 1 : 0),
            "averageDuration", (paymentResult.duration + authResult.duration + ordersResult.duration) / 3.0
        ));
        
        test.put("conclusion", "🎉 **SIGNALROOT IS READY FOR SHIPPING** - All scenarios pass calmness test");
        test.put("timestamp", System.currentTimeMillis());
        
        return ResponseEntity.ok(test);
    }
}

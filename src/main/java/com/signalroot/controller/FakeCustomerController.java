package com.signalroot.controller;

import com.signalroot.service.FakeCustomerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/fake-customers")
public class FakeCustomerController {
    
    private static final Logger logger = LoggerFactory.getLogger(FakeCustomerController.class);
    
    @Autowired
    private FakeCustomerService fakeCustomerService;
    
    @GetMapping("/teams")
    public ResponseEntity<List<FakeCustomerService.FakeTeam>> getAllTeams() {
        logger.info("Fetching all fake customer teams");
        return ResponseEntity.ok(fakeCustomerService.getAllTeams());
    }
    
    @GetMapping("/teams/{teamId}")
    public ResponseEntity<FakeCustomerService.FakeTeam> getTeam(@PathVariable String teamId) {
        logger.info("Fetching fake customer team: {}", teamId);
        
        FakeCustomerService.FakeTeam team = fakeCustomerService.getTeam(teamId);
        if (team == null) {
            return ResponseEntity.notFound().build();
        }
        
        return ResponseEntity.ok(team);
    }
    
    @GetMapping("/teams/{teamId}/services")
    public ResponseEntity<List<FakeCustomerService.Service>> getTeamServices(@PathVariable String teamId) {
        logger.info("Fetching services for team: {}", teamId);
        
        List<FakeCustomerService.Service> services = fakeCustomerService.getTeamServices(teamId);
        return ResponseEntity.ok(services);
    }
    
    @GetMapping("/teams/{teamId}/stats")
    public ResponseEntity<FakeCustomerService.TeamStats> getTeamStats(@PathVariable String teamId) {
        logger.info("Fetching stats for team: {}", teamId);
        
        FakeCustomerService.TeamStats stats = fakeCustomerService.getTeamStats(teamId);
        if (stats == null) {
            return ResponseEntity.notFound().build();
        }
        
        return ResponseEntity.ok(stats);
    }
    
    @PostMapping("/generate-alerts")
    public ResponseEntity<Map<String, Object>> generateRandomAlerts() {
        logger.info("Generating random alerts for all fake customer teams");
        
        fakeCustomerService.generateRandomAlerts();
        
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Random alerts generated successfully");
        response.put("teams", fakeCustomerService.getAllTeams().size());
        response.put("timestamp", System.currentTimeMillis());
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/overview")
    public ResponseEntity<Map<String, Object>> getOverview() {
        logger.info("Fetching fake customer overview");
        
        List<FakeCustomerService.FakeTeam> teams = fakeCustomerService.getAllTeams();
        
        Map<String, Object> overview = new HashMap<>();
        overview.put("totalTeams", teams.size());
        overview.put("totalServices", teams.stream().mapToInt(t -> t.services.size()).sum());
        overview.put("teams", teams.stream().map(t -> Map.of(
            "id", t.id,
            "name", t.name,
            "serviceCount", t.services.size(),
            "alertFrequency", t.alertFrequency.toString()
        )).toList());
        overview.put("timestamp", System.currentTimeMillis());
        
        return ResponseEntity.ok(overview);
    }
    
    @PostMapping("/teams/{teamId}/simulate-alert")
    public ResponseEntity<Map<String, Object>> simulateAlert(@PathVariable String teamId, 
                                                           @RequestBody Map<String, Object> payload) {
        logger.info("Simulating alert for team: {}", teamId);
        
        FakeCustomerService.FakeTeam team = fakeCustomerService.getTeam(teamId);
        if (team == null) {
            return ResponseEntity.notFound().build();
        }
        
        // Simulate alert processing
        Map<String, Object> response = new HashMap<>();
        response.put("teamId", teamId);
        response.put("teamName", team.name);
        response.put("message", "Alert simulation completed");
        response.put("services", team.services.size());
        response.put("alertFrequency", team.alertFrequency.toString());
        response.put("timestamp", System.currentTimeMillis());
        
        return ResponseEntity.ok(response);
    }
}

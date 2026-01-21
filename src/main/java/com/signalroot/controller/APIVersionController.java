package com.signalroot.controller;

import com.signalroot.service.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * REST controller for API versioning and documentation
 */
@RestController
@RequestMapping("/api/versions")
@Tag(name = "versions", description = "API versioning information")
@CrossOrigin(origins = "*", maxAge = 3600)
public class APIVersionController {
    
    @Autowired
    @Qualifier("mockTenantAlertService")
    private TenantAlertServiceInterface alertService;
    
    @Autowired
    @Qualifier("mockTenantDeployService") 
    private TenantDeployServiceInterface deployService;
    
    @Autowired
    @Qualifier("mockOrganizationService")
    private OrganizationServiceInterface organizationService;
    
    @Autowired
    private DogfoodingService dogfoodingService;
    
    @Autowired
    private IdempotencyService idempotencyService;
    
    /**
     * Get API version history
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getVersions() {
        try {
            // Mock version history
            Map<String, Object> response = new HashMap<>();
            
            // Create mock version data
            Map<String, Object> versions = new HashMap<>();
            
            // Version 1.0.0
            Map<String, Object> v1_0_0 = new HashMap<>();
            v1_0_0.put("version", "v1.0.0");
            v1_0_0.put("releasedAt", "2024-01-20T00:00:00Z");
            v1_0_0.put("description", "Initial SignalRoot API release with core alert enrichment functionality");
            v1_0_0.put("isCurrent", false);
            
            List<Map<String, Object>> v1_0_0_changes = new ArrayList<>();
            Map<String, Object> change1 = new HashMap<>();
            change1.put("type", "feature");
            change1.put("description", "Added API documentation and webhook integration guides");
            change1.put("date", "2024-01-20T00:00:00Z");
            v1_0_0_changes.add(change1);
            v1_0_0.put("changes", v1_0_0_changes);
            
            // Version 1.1.0
            Map<String, Object> v1_1_0 = new HashMap<>();
            v1_1_0.put("version", "v1.1.0");
            v1_1_0.put("releasedAt", "2024-01-20T00:00:00Z");
            v1_1_0.put("description", "Added interactive API explorer and changelog component");
            v1_1_0.put("isCurrent", true);
            
            List<Map<String, Object>> v1_1_0_changes = new ArrayList<>();
            Map<String, Object> change2 = new HashMap<>();
            change2.put("type", "feature");
            change2.put("description", "Enhanced webhook integration with configuration forms");
            change2.put("date", "2024-01-20T00:00:00Z");
            v1_1_0_changes.add(change2);
            v1_1_0.put("changes", v1_1_0_changes);
            
            versions.put("v1.0.0", v1_0_0);
            versions.put("v1.1.0", v1_1_0);
            
            response.put("versions", versions);
            
            response.put("currentVersion", "v1.1.0");
            response.put("message", "Version history retrieved successfully");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to retrieve version history");
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }
    
    /**
     * Get changelog for a specific version
     */
    @GetMapping("/changelog/{version}")
    public ResponseEntity<Map<String, Object>> getChangelog(@PathVariable String version) {
        try {
            // Find version in mock data
            Map<String, Object> mockVersions = new HashMap<>();
            
            // Version 1.0.0
            Map<String, Object> v1_0_0_changelog = new HashMap<>();
            v1_0_0_changelog.put("version", "v1.0.0");
            v1_0_0_changelog.put("releasedAt", "2024-01-20T00:00:00Z");
            v1_0_0_changelog.put("description", "Initial SignalRoot API release with core alert enrichment functionality");
            v1_0_0_changelog.put("changes", new ArrayList<>());
            
            // Version 1.1.0
            Map<String, Object> v1_1_0_changelog = new HashMap<>();
            v1_1_0_changelog.put("version", "v1.1.0");
            v1_1_0_changelog.put("releasedAt", "2024-01-20T00:00:00Z");
            v1_1_0_changelog.put("description", "Added interactive API explorer and changelog component");
            
            List<Map<String, Object>> v1_1_0_changelog_changes = new ArrayList<>();
            Map<String, Object> changelog_change = new HashMap<>();
            changelog_change.put("type", "feature");
            changelog_change.put("description", "Enhanced webhook integration with configuration forms");
            changelog_change.put("date", "2024-01-20T00:00:00Z");
            v1_1_0_changelog_changes.add(changelog_change);
            v1_1_0_changelog.put("changes", v1_1_0_changelog_changes);
            
            mockVersions.put("v1.0.0", v1_0_0_changelog);
            mockVersions.put("v1.1.0", v1_1_0_changelog);
            
            Map<String, Object> versionData = (Map<String, Object>) mockVersions.get(version);
            
            if (versionData == null) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("error", "Version not found");
                errorResponse.put("message", "Version " + version + " not found");
                return ResponseEntity.status(404).body(errorResponse);
            }
            
            Map<String, Object> changelog = (Map<String, Object>) versionData.get("changes");
            
            Map<String, Object> response = new HashMap<>();
            response.put("version", version);
            response.put("description", versionData.get("description"));
            response.put("releasedAt", versionData.get("releasedAt"));
            response.put("isCurrent", versionData.get("isCurrent"));
            response.put("changes", changelog);
            response.put("message", "Changelog retrieved successfully");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to retrieve changelog for version " + version);
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }
    
    /**
     * Get current API version
     */
    @GetMapping("/version/current")
    public ResponseEntity<Map<String, Object>> getCurrentVersion() {
        try {
            // Find version in mock data
            Map<String, Object> mockVersions = new HashMap<>();
            
            // Version 1.0.0
            Map<String, Object> v1_0_0_current = new HashMap<>();
            v1_0_0_current.put("version", "v1.0.0");
            v1_0_0_current.put("releasedAt", "2024-01-20T00:00:00Z");
            v1_0_0_current.put("description", "Initial SignalRoot API release with core alert enrichment functionality");
            v1_0_0_current.put("changes", new ArrayList<>());
            
            // Version 1.1.0
            Map<String, Object> v1_1_0_current = new HashMap<>();
            v1_1_0_current.put("version", "v1.1.0");
            v1_1_0_current.put("releasedAt", "2024-01-20T00:00:00Z");
            v1_1_0_current.put("description", "Added interactive API explorer and changelog component");
            
            List<Map<String, Object>> v1_1_0_current_changes = new ArrayList<>();
            Map<String, Object> current_change = new HashMap<>();
            current_change.put("type", "feature");
            current_change.put("description", "Enhanced webhook integration with configuration forms");
            current_change.put("date", "2024-01-20T00:00:00Z");
            v1_1_0_current_changes.add(current_change);
            v1_1_0_current.put("changes", v1_1_0_current_changes);
            
            mockVersions.put("v1.0.0", v1_0_0_current);
            mockVersions.put("v1.1.0", v1_1_0_current);
            
            Map<String, Object> response = new HashMap<>();
            response.put("version", "v1.1.0");
            response.put("description", "v1.1.0 - Added interactive API explorer and changelog component");
            response.put("releasedAt", "2024-01-20T00:00:00Z");
            response.put("isCurrent", true);
            response.put("message", "Current version retrieved successfully");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to get current version");
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }
    
    /**
     * Test endpoint to verify controller is working
     */
    @GetMapping("/test")
    public ResponseEntity<Map<String, Object>> test() {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "APIVersionController is working correctly");
        response.put("timestamp", java.time.Instant.now().toString());
        return ResponseEntity.ok(response);
    }
}

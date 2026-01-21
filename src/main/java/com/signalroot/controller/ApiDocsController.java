package com.signalroot.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Controller to handle API documentation redirects
 */
@RestController
@RequestMapping("/api-docs")
@Tag(name = "api-docs", description = "API documentation redirects")
@CrossOrigin(origins = "*", maxAge = 3600)
public class ApiDocsController {

    /**
     * Redirect /api-docs to /v3/api-docs
     */
    @GetMapping
    @Operation(summary = "Redirect to API documentation", description = "Redirects to the v3 API documentation endpoint")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "302", description = "Redirect to /v3/api-docs")
    })
    public ResponseEntity<Void> redirectToApiDocs() {
        return ResponseEntity.status(HttpStatus.FOUND)
                .location(java.net.URI.create("/v3/api-docs"))
                .build();
    }

    /**
     * Handle /api-docs/** requests by redirecting to /v3/api-docs/**
     */
    @GetMapping("/**")
    @Operation(summary = "Redirect API documentation paths", description = "Redirects any /api-docs/** paths to /v3/api-docs/**")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "302", description = "Redirect to corresponding /v3/api-docs/** path")
    })
    public ResponseEntity<Void> redirectToApiDocsPath(HttpServletRequest request) {
        String path = request.getRequestURI();
        String redirectPath = path.replace("/api-docs", "/v3/api-docs");
        return ResponseEntity.status(HttpStatus.FOUND)
                .location(java.net.URI.create(redirectPath))
                .build();
    }
}

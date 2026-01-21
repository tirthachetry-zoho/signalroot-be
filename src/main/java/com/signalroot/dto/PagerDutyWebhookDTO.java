package com.signalroot.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;

public class PagerDutyWebhookDTO {
    
    @NotBlank
    private String type;
    
    @NotBlank
    private String webhookId;
    
    @NotNull
    private Payload payload;
    
    public static class Payload {
        @NotBlank
        private String type;
        
        @NotNull
        private Incident incident;
        
        @NotNull
        private Instant created_at;
        
        public static class Incident {
            @NotBlank
            private String id;
            
            @NotBlank
            private String title;
            
            private String description;
            
            @NotNull
            private IncidentStatus status;
            
            @NotNull
            private IncidentSeverity severity;
            
            @NotNull
            private Service service;
            
            @NotNull
            private Instant created_at;
            
            public static class Service {
                @NotBlank
                private String id;
                
                @NotBlank
                private String name;
                
                private String description;
                
                public String getId() { return id; }
                public void setId(String id) { this.id = id; }
                public String getName() { return name; }
                public void setName(String name) { this.name = name; }
                public String getDescription() { return description; }
                public void setDescription(String description) { this.description = description; }
            }
            
            public enum IncidentStatus {
                @JsonProperty("triggered") TRIGGERED,
                @JsonProperty("acknowledged") ACKNOWLEDGED,
                @JsonProperty("resolved") RESOLVED
            }
            
            public enum IncidentSeverity {
                @JsonProperty("critical") CRITICAL,
                @JsonProperty("high") HIGH,
                @JsonProperty("warning") WARNING,
                @JsonProperty("info") INFO
            }
            
            public String getId() { return id; }
            public void setId(String id) { this.id = id; }
            public String getTitle() { return title; }
            public void setTitle(String title) { this.title = title; }
            public String getDescription() { return description; }
            public void setDescription(String description) { this.description = description; }
            public IncidentStatus getStatus() { return status; }
            public void setStatus(IncidentStatus status) { this.status = status; }
            public IncidentSeverity getSeverity() { return severity; }
            public void setSeverity(IncidentSeverity severity) { this.severity = severity; }
            public Service getService() { return service; }
            public void setService(Service service) { this.service = service; }
            public Instant getCreated_at() { return created_at; }
            public void setCreated_at(Instant created_at) { this.created_at = created_at; }
        }
        
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        public Incident getIncident() { return incident; }
        public void setIncident(Incident incident) { this.incident = incident; }
        public Instant getCreated_at() { return created_at; }
        public void setCreated_at(Instant created_at) { this.created_at = created_at; }
    }
    
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getWebhookId() { return webhookId; }
    public void setWebhookId(String webhookId) { this.webhookId = webhookId; }
    public Payload getPayload() { return payload; }
    public void setPayload(Payload payload) { this.payload = payload; }
}

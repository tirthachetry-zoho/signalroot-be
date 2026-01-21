package com.signalroot.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;
import java.util.Map;

public class JenkinsDeployWebhookDTO {
    
    @NotBlank
    private String name;
    
    @NotNull
    private Build build;
    
    @NotNull
    private Job job;
    
    public static class Build {
        @NotBlank
        private String number;
        
        @NotBlank
        private String url;
        
        @NotNull
        private BuildPhase phase;
        
        @NotNull
        private BuildStatus status;
        
        @NotNull
        private Instant timestamp;
        
        private Long duration;
        
        private String description;
        
        private Map<String, Object> parameters;
        
        public enum BuildPhase {
            @JsonProperty("STARTED") STARTED,
            @JsonProperty("COMPLETED") COMPLETED,
            @JsonProperty("FINISHED") FINISHED
        }
        
        public enum BuildStatus {
            @JsonProperty("SUCCESS") SUCCESS,
            @JsonProperty("FAILURE") FAILURE,
            @JsonProperty("ABORTED") ABORTED,
            @JsonProperty("UNSTABLE") UNSTABLE
        }
        
        public String getNumber() { return number; }
        public void setNumber(String number) { this.number = number; }
        public String getUrl() { return url; }
        public void setUrl(String url) { this.url = url; }
        public BuildPhase getPhase() { return phase; }
        public void setPhase(BuildPhase phase) { this.phase = phase; }
        public BuildStatus getStatus() { return status; }
        public void setStatus(BuildStatus status) { this.status = status; }
        public Instant getTimestamp() { return timestamp; }
        public void setTimestamp(Instant timestamp) { this.timestamp = timestamp; }
        public Long getDuration() { return duration; }
        public void setDuration(Long duration) { this.duration = duration; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public Map<String, Object> getParameters() { return parameters; }
        public void setParameters(Map<String, Object> parameters) { this.parameters = parameters; }
    }
    
    public static class Job {
        @NotBlank
        private String name;
        
        private String displayName;
        
        private String description;
        
        @NotBlank
        private String url;
        
        private Map<String, String> properties;
        
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getDisplayName() { return displayName; }
        public void setDisplayName(String displayName) { this.displayName = displayName; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public String getUrl() { return url; }
        public void setUrl(String url) { this.url = url; }
        public Map<String, String> getProperties() { return properties; }
        public void setProperties(Map<String, String> properties) { this.properties = properties; }
    }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Build getBuild() { return build; }
    public void setBuild(Build build) { this.build = build; }
    public Job getJob() { return job; }
    public void setJob(Job job) { this.job = job; }
}

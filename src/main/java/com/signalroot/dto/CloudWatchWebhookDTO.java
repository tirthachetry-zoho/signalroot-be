package com.signalroot.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;
import java.util.List;
import java.util.Map;

public class CloudWatchWebhookDTO {
    
    @NotBlank
    private String version;
    
    @NotBlank
    private String id;
    
    @NotBlank
    private String detailType;
    
    @NotBlank
    private String source;
    
    @NotNull
    private String account;
    
    @NotNull
    private Instant time;
    
    @NotBlank
    private String region;
    
    @NotNull
    private List<String> resources;
    
    @NotNull
    private Map<String, Object> detail;
    
    public static class AlarmDetail {
        @NotBlank
        private String alarmName;
        
        @NotNull
        private AlarmState state;
        
        private String stateReason;
        
        private String stateReasonData;
        
        @NotNull
        private Instant stateChangedTime;
        
        private String configuration;
        
        private Map<String, String> dimensions;
        
        public enum AlarmState {
            @JsonProperty("OK") OK,
            @JsonProperty("ALARM") ALARM,
            @JsonProperty("INSUFFICIENT_DATA") INSUFFICIENT_DATA
        }
        
        public String getAlarmName() { return alarmName; }
        public void setAlarmName(String alarmName) { this.alarmName = alarmName; }
        public AlarmState getState() { return state; }
        public void setState(AlarmState state) { this.state = state; }
        public String getStateReason() { return stateReason; }
        public void setStateReason(String stateReason) { this.stateReason = stateReason; }
        public String getStateReasonData() { return stateReasonData; }
        public void setStateReasonData(String stateReasonData) { this.stateReasonData = stateReasonData; }
        public Instant getStateChangedTime() { return stateChangedTime; }
        public void setStateChangedTime(Instant stateChangedTime) { this.stateChangedTime = stateChangedTime; }
        public String getConfiguration() { return configuration; }
        public void setConfiguration(String configuration) { this.configuration = configuration; }
        public Map<String, String> getDimensions() { return dimensions; }
        public void setDimensions(Map<String, String> dimensions) { this.dimensions = dimensions; }
    }
    
    public String getVersion() { return version; }
    public void setVersion(String version) { this.version = version; }
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getDetailType() { return detailType; }
    public void setDetailType(String detailType) { this.detailType = detailType; }
    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }
    public String getAccount() { return account; }
    public void setAccount(String account) { this.account = account; }
    public Instant getTime() { return time; }
    public void setTime(Instant time) { this.time = time; }
    public String getRegion() { return region; }
    public void setRegion(String region) { this.region = region; }
    public List<String> getResources() { return resources; }
    public void setResources(List<String> resources) { this.resources = resources; }
    public Map<String, Object> getDetail() { return detail; }
    public void setDetail(Map<String, Object> detail) { this.detail = detail; }
}

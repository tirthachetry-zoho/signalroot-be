package com.signalroot.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;

public class GitHubDeployWebhookDTO {
    
    @NotBlank
    private String action;
    
    @NotNull
    private Deployment deployment;
    
    @NotNull
    private Repository repository;
    
    private User sender;
    
    public static class Deployment {
        @NotBlank
        private String url;
        
        @NotBlank
        private String id;
        
        @NotBlank
        private String sha;
        
        @NotBlank
        private String ref;
        
        private String task;
        
        private String environment;
        
        private String description;
        
        @NotNull
        private DeploymentStatus status;
        
        @NotNull
        private Instant created_at;
        
        private Instant updated_at;
        
        private User creator;
        
        public static class DeploymentStatus {
            @NotBlank
            private String state;
            
            private String description;
            
            @NotNull
            private Instant created_at;
            
            private Instant updated_at;
            
            private User creator;
            
            public String getState() { return state; }
            public void setState(String state) { this.state = state; }
            public String getDescription() { return description; }
            public void setDescription(String description) { this.description = description; }
            public Instant getCreated_at() { return created_at; }
            public void setCreated_at(Instant created_at) { this.created_at = created_at; }
            public Instant getUpdated_at() { return updated_at; }
            public void setUpdated_at(Instant updated_at) { this.updated_at = updated_at; }
            public User getCreator() { return creator; }
            public void setCreator(User creator) { this.creator = creator; }
        }
        
        public String getUrl() { return url; }
        public void setUrl(String url) { this.url = url; }
        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        public String getSha() { return sha; }
        public void setSha(String sha) { this.sha = sha; }
        public String getRef() { return ref; }
        public void setRef(String ref) { this.ref = ref; }
        public String getTask() { return task; }
        public void setTask(String task) { this.task = task; }
        public String getEnvironment() { return environment; }
        public void setEnvironment(String environment) { this.environment = environment; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public DeploymentStatus getStatus() { return status; }
        public void setStatus(DeploymentStatus status) { this.status = status; }
        public Instant getCreated_at() { return created_at; }
        public void setCreated_at(Instant created_at) { this.created_at = created_at; }
        public Instant getUpdated_at() { return updated_at; }
        public void setUpdated_at(Instant updated_at) { this.updated_at = updated_at; }
        public User getCreator() { return creator; }
        public void setCreator(User creator) { this.creator = creator; }
    }
    
    public static class Repository {
        @NotBlank
        private String id;
        
        @NotBlank
        private String name;
        
        @NotBlank
        private String full_name;
        
        private String description;
        
        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getFull_name() { return full_name; }
        public void setFull_name(String full_name) { this.full_name = full_name; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
    }
    
    public static class User {
        @NotBlank
        private String login;
        
        private String name;
        
        private String email;
        
        public String getLogin() { return login; }
        public void setLogin(String login) { this.login = login; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
    }
    
    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }
    public Deployment getDeployment() { return deployment; }
    public void setDeployment(Deployment deployment) { this.deployment = deployment; }
    public Repository getRepository() { return repository; }
    public void setRepository(Repository repository) { this.repository = repository; }
    public User getSender() { return sender; }
    public void setSender(User sender) { this.sender = sender; }
}

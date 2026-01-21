package com.signalroot.config;

import com.signalroot.service.*;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

@Configuration
public class ApplicationConfig {
    
    @Bean
    @Primary
    @ConditionalOnProperty(name = "signalroot.service.mode", havingValue = "mock", matchIfMissing = true)
    public OrganizationServiceInterface mockOrganizationService() {
        return new MockOrganizationService();
    }
    
    @Bean
    @ConditionalOnProperty(name = "signalroot.service.mode", havingValue = "real")
    public OrganizationServiceInterface realOrganizationService() {
        return new DatabaseOrganizationService();
    }
    
    @Bean
    @Primary
    @ConditionalOnProperty(name = "signalroot.service.mode", havingValue = "mock", matchIfMissing = true)
    public TenantAlertServiceInterface mockAlertService() {
        return new MockTenantAlertService();
    }
    
    @Bean
    @ConditionalOnProperty(name = "signalroot.service.mode", havingValue = "real")
    public TenantAlertServiceInterface realAlertService() {
        return new TenantAlertService();
    }
    
    @Bean
    @Primary
    @ConditionalOnProperty(name = "signalroot.service.mode", havingValue = "mock", matchIfMissing = true)
    public TenantDeployServiceInterface mockDeployService() {
        return new MockTenantDeployService();
    }
    
    @Bean
    @ConditionalOnProperty(name = "signalroot.service.mode", havingValue = "real")
    public TenantDeployServiceInterface realDeployService() {
        return new TenantDeployService();
    }
    
    // Explicit bean definitions for mock services
    @Bean
    @Primary
    @ConditionalOnProperty(name = "signalroot.service.mode", havingValue = "mock", matchIfMissing = true)
    public MockAlertService mockAlertServicePrimary() {
        return new MockAlertService();
    }
    
    @Bean
    @Primary
    @ConditionalOnProperty(name = "signalroot.service.mode", havingValue = "mock", matchIfMissing = true)
    public MockGitHubService mockGitHubServicePrimary() {
        return new MockGitHubService();
    }
    
    // Notification service beans
    @Bean
    @ConditionalOnProperty(name = "signalroot.notifications.email.enabled", havingValue = "true")
    public EmailNotifier emailNotifier() {
        return new EmailNotifier();
    }
    
    @Bean
    @ConditionalOnProperty(name = "signalroot.notifications.slack.enabled", havingValue = "true")
    public SlackNotifier slackNotifier() {
        return new SlackNotifier();
    }
}

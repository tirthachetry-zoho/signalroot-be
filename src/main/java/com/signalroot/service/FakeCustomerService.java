package com.signalroot.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class FakeCustomerService {
    
    private static final Logger logger = LoggerFactory.getLogger(FakeCustomerService.class);
    
    // Fake customer teams
    private final Map<String, FakeTeam> teams = new HashMap<>();
    
    public FakeCustomerService() {
        initializeFakeCustomers();
    }
    
    private void initializeFakeCustomers() {
        // Payments Team
        FakeTeam paymentsTeam = new FakeTeam();
        paymentsTeam.id = "team-payments";
        paymentsTeam.name = "Payments Team";
        paymentsTeam.description = "Handles payment processing and financial transactions";
        paymentsTeam.alertFrequency = AlertFrequency.HIGH;
        paymentsTeam.services = Arrays.asList(
            new Service("payment-service", "Core payment processing API"),
            new Service("billing-service", "Billing and invoicing system"),
            new Service("fraud-detection", "Fraud detection and prevention")
        );
        teams.put(paymentsTeam.id, paymentsTeam);
        
        // Auth Team  
        AuthTeam authTeam = new AuthTeam();
        authTeam.id = "team-auth";
        authTeam.name = "Auth Team";
        authTeam.description = "Manages authentication and authorization";
        authTeam.alertFrequency = AlertFrequency.MEDIUM;
        authTeam.services = Arrays.asList(
            new Service("auth-service", "User authentication service"),
            new Service("oauth-service", "OAuth and SSO integration"),
            new Service("session-service", "Session management")
        );
        teams.put(authTeam.id, authTeam);
        
        // Orders Team
        OrdersTeam ordersTeam = new OrdersTeam();
        ordersTeam.id = "team-orders";
        ordersTeam.name = "Orders Team";
        ordersTeam.description = "Manages order processing and fulfillment";
        ordersTeam.alertFrequency = AlertFrequency.LOW;
        ordersTeam.services = Arrays.asList(
            new Service("order-service", "Order processing system"),
            new Service("inventory-service", "Inventory management"),
            new Service("shipping-service", "Shipping and fulfillment")
        );
        teams.put(ordersTeam.id, ordersTeam);
        
        logger.info("Initialized {} fake customer teams", teams.size());
    }
    
    public List<FakeTeam> getAllTeams() {
        return new ArrayList<>(teams.values());
    }
    
    public FakeTeam getTeam(String teamId) {
        return teams.get(teamId);
    }
    
    public List<Service> getTeamServices(String teamId) {
        FakeTeam team = teams.get(teamId);
        return team != null ? team.services : Collections.emptyList();
    }
    
    public void generateRandomAlerts() {
        for (FakeTeam team : teams.values()) {
            int alertCount = getRandomAlertCount(team.alertFrequency);
            
            for (int i = 0; i < alertCount; i++) {
                Service service = getRandomService(team.services);
                Alert alert = generateRandomAlert(service, team);
                
                logger.info("Generated fake alert for {}: {} - {}", 
                           team.name, service.name, alert.title);
            }
        }
    }
    
    private int getRandomAlertCount(AlertFrequency frequency) {
        return switch (frequency) {
            case HIGH -> ThreadLocalRandom.current().nextInt(3, 8); // 3-7 alerts
            case MEDIUM -> ThreadLocalRandom.current().nextInt(2, 5); // 2-4 alerts  
            case LOW -> ThreadLocalRandom.current().nextInt(1, 3); // 1-2 alerts
        };
    }
    
    private Service getRandomService(List<Service> services) {
        return services.get(ThreadLocalRandom.current().nextInt(services.size()));
    }
    
    private Alert generateRandomAlert(Service service, FakeTeam team) {
        Alert alert = new Alert();
        alert.id = UUID.randomUUID().toString();
        alert.service = service.name;
        alert.team = team.name;
        alert.severity = getRandomSeverity();
        alert.title = getRandomAlertTitle(service);
        alert.description = getRandomAlertDescription(service);
        alert.timestamp = LocalDateTime.now().minusMinutes(ThreadLocalRandom.current().nextInt(60));
        alert.status = getRandomStatus();
        
        return alert;
    }
    
    private String getRandomSeverity() {
        String[] severities = {"CRITICAL", "HIGH", "MEDIUM", "LOW"};
        return severities[ThreadLocalRandom.current().nextInt(severities.length)];
    }
    
    private String getRandomAlertTitle(Service service) {
        String[] templates = {
            "High latency detected in {}",
            "{} service experiencing errors", 
            "Memory usage spike in {}",
            "{} database connection issues",
            "CPU utilization high in {}"
        };
        
        String template = templates[ThreadLocalRandom.current().nextInt(templates.length)];
        return template.replace("{}", service.name);
    }
    
    private String getRandomAlertDescription(Service service) {
        return String.format("The %s is showing elevated error rates. " +
                           "Current metrics indicate potential performance degradation. " +
                           "Immediate investigation recommended.", service.name);
    }
    
    private String getRandomStatus() {
        return ThreadLocalRandom.current().nextBoolean() ? "FIRING" : "RESOLVED";
    }
    
    public TeamStats getTeamStats(String teamId) {
        FakeTeam team = teams.get(teamId);
        if (team == null) return null;
        
        TeamStats stats = new TeamStats();
        stats.teamId = team.id;
        stats.teamName = team.name;
        stats.serviceCount = team.services.size();
        stats.alertFrequency = team.alertFrequency.toString();
        stats.lastAlertTime = LocalDateTime.now().minusMinutes(ThreadLocalRandom.current().nextInt(30));
        
        return stats;
    }
    
    // Data classes
    public static class FakeTeam {
        public String id;
        public String name;
        public String description;
        public AlertFrequency alertFrequency;
        public List<Service> services;
    }
    
    public static class AuthTeam extends FakeTeam {
        // Auth team specific properties
    }
    
    public static class PaymentsTeam extends FakeTeam {
        // Payments team specific properties  
    }
    
    public static class OrdersTeam extends FakeTeam {
        // Orders team specific properties
    }
    
    public static class Service {
        public String name;
        public String description;
        
        public Service(String name, String description) {
            this.name = name;
            this.description = description;
        }
    }
    
    public static class Alert {
        public String id;
        public String service;
        public String team;
        public String severity;
        public String title;
        public String description;
        public LocalDateTime timestamp;
        public String status;
    }
    
    public static class TeamStats {
        public String teamId;
        public String teamName;
        public int serviceCount;
        public String alertFrequency;
        public LocalDateTime lastAlertTime;
    }
    
    public enum AlertFrequency {
        HIGH, MEDIUM, LOW
    }
}

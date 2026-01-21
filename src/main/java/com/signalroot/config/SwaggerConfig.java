package com.signalroot.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.tags.Tag;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("SignalRoot API")
                        .description("SignalRoot API for alert management and webhook processing")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("SignalRoot Team")
                                .email("support@signalroot.com")
                                .url("https://signalroot.com"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8080")
                                .description("Development server"),
                        new Server()
                                .url("https://api.signalroot.com")
                                .description("Production server")
                ))
                .tags(List.of(
                        new Tag()
                                .name("dogfooding")
                                .description("Dogfooding scenario management and testing"),
                        new Tag()
                                .name("webhooks")
                                .description("Webhook processing and management"),
                        new Tag()
                                .name("safety")
                                .description("Safety and idempotency features"),
                        new Tag()
                                .name("versions")
                                .description("API versioning information")
                ));
    }
}

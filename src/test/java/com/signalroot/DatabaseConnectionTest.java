package com.signalroot;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:postgresql://localhost:5432/signalroot",
    "spring.datasource.username=test_user",
    "spring.datasource.password=test_password",
    "spring.jpa.hibernate.ddl-auto=none"
})
public class DatabaseConnectionTest {

    @Test
    public void testDatabaseConnection() {
        System.out.println("Testing database connection...");
        // This test will fail if the database connection doesn't work
    }
}

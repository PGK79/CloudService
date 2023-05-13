package ru.netology.cloudservice;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Map;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CloudserviceApplicationTests {
    private static final Network network = Network.newNetwork();

    @Autowired
    private TestRestTemplate restTemplate;

    private static final MySQLContainer<?> mysql = new MySQLContainer<>("mysql")
            .withExposedPorts(3306)
            .withEnv("MYSQL_DB", "cloud_database")
            .withEnv("MYSQL_USER", "root")
            .withEnv("MYSQL_ROOT_PASSWORD", "mysql")
            .withEnv("MYSQL_DATA", "/var/lib/data/mysqldata")
            .withNetwork(network)
            .withNetworkAliases("mysql");

    @Container
    private static final GenericContainer<?> backendApp =
            new GenericContainer<>("cloudbackend:1.0")
                    .withExposedPorts(8081)
                    .dependsOn(mysql)
                    .withEnv(Map.of("SPRING_DATASOURCE_URL", "jdbc:mysql://localhost:3306/cloud_database"))
                    .withNetwork(network);

    @Test
    void contextDatabase() {
        Assertions.assertTrue(mysql.isRunning());
    }

    @Test
    void contextServer() {
        Assertions.assertFalse(backendApp.isRunning());
    }
}

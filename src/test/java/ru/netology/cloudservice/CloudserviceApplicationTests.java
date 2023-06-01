package ru.netology.cloudservice;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.netology.cloudservice.entity.User;
import ru.netology.cloudservice.repository.UserRepository;

import java.util.Map;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CloudserviceApplicationTests {
    private static final Network network = Network.newNetwork();
    private final String token = "token";
    private final User user = new User("test@mail.com", "test", token);

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }
    @Container
    public static MySQLContainer<?> mysql = new MySQLContainer<>("mysql")
            .withNetwork(network)
            .withExposedPorts(3306)
            .withDatabaseName("cloud_database")
            .withUsername("test")
            .withPassword("test");

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
    @Test
    void testGetUserByLoginAndPassword(){
        //given
        userRepository.save(user);
        User expected = user;

        // when:
        User actual = userRepository.findByLoginAndPassword("test@mail.com", "test").get();

        // when:
        Assertions.assertEquals(expected,actual);
    }

    @Test
    void testGetUserByAuthToken(){
        //given
        userRepository.save(user);
        User expected = user;

        // when:
        User actual = userRepository.findUserByAuthToken("token").get();

        // when:
        Assertions.assertEquals(expected,actual);
    }
}

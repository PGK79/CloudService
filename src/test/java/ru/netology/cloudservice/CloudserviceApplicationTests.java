package ru.netology.cloudservice;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.data.domain.PageRequest;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.netology.cloudservice.entity.File;
import ru.netology.cloudservice.entity.User;
import ru.netology.cloudservice.repository.FileRepository;
import ru.netology.cloudservice.repository.UserRepository;

import java.awt.print.Pageable;
import java.util.List;
import java.util.Map;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CloudserviceApplicationTests {
    private static final Network network = Network.newNetwork();
    private final String token = "token";
    private final String filename = "filename";
    private final User user = new User("test@mail.com", "test", token);
    private final File fileOne = new File("file content".getBytes(),12L, filename, user);
    private final File fileTwo = new File("file content two".getBytes(),16L, "new" + filename, user);

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    UserRepository userRepository;

    @Autowired
    FileRepository fileRepository;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        fileRepository.deleteAll();
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

    @Test
    void testGetFileByNameAndUser(){
        //given
        userRepository.save(user);
        fileRepository.save(fileOne);
        File expected = fileOne;

        // when:
        File actual = fileRepository.findFileByNameAndUser(filename,user).get();

        // when:
        Assertions.assertEquals(expected,actual);
    }
    @Test
    void testGetFile(){
        //given
        userRepository.save(user);
        fileRepository.save(fileOne);
        fileRepository.save(fileTwo);
        List<File> expected = List.of(fileOne,fileTwo);

        // when:
        List<File> actual = fileRepository.findAllFilesByUser(user,PageRequest.of(0, 2));

        // when:
        Assertions.assertEquals(expected,actual);
    }
}

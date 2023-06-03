package ru.netology.cloudservice.integration_test;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.netology.cloudservice.entity.File;
import ru.netology.cloudservice.entity.User;
import ru.netology.cloudservice.repository.FileRepository;
import ru.netology.cloudservice.repository.UserRepository;

import java.util.List;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class DaoTest {
    private final String filename = "filename";
    private final String login = "ivan@mail.com";
    private final String password = "ivan";
    private final String token = "token";
    private final User user = new User(1L, login, password, token);
    private final File fileOne = new File(1, "file content".getBytes(), 12L, filename, user);
    private final File fileTwo = new File(2, "new file content".getBytes(), 16L, "new" + filename, user);

    @Autowired
    UserRepository userRepository;

    @Autowired
    FileRepository fileRepository;

    @Container
    private static MySQLContainer<?> database = new MySQLContainer<>("mysql")
            .withDatabaseName("cloud_database")
            .withUsername("test")
            .withPassword("test")
            .withInitScript("db.sql");

    @DynamicPropertySource
    public static void properties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", database::getJdbcUrl);
        registry.add("spring.datasource.username", database::getUsername);
        registry.add("spring.datasource.password", database::getPassword);
    }

    @Test
    public void contextDatabase() {
        Assertions.assertTrue(database.isRunning());
    }

    @Test
    public void testGetUserByLoginAndPassword() {
        //given
        User expected = user;

        // when:
        User actual = userRepository.findByLoginAndPassword("ivan@mail.com", "ivan").get();

        // then:
        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void testGetUserByAuthToken() {
        //given
        User expected = user;

        // when:
        User actual = userRepository.findUserByAuthToken("token").get();

        // then:
        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void testGetFileByNameAndUser() {
        //given
        File expected = fileOne;

        // when:
        File actual = fileRepository.findFileByNameAndUser(filename, user).get();

        // then:
        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void testGetFile() {
        //given
        List<File> expected = List.of(fileOne, fileTwo);

        // when:
        List<File> actual = fileRepository.findAllFilesByUser(user, PageRequest.of(0, 2));

        // then:
        Assertions.assertEquals(expected, actual);
    }
}
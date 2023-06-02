package ru.netology.cloudservice;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.netology.cloudservice.cryptograph.Crypter;
import ru.netology.cloudservice.entity.File;
import ru.netology.cloudservice.entity.User;
import ru.netology.cloudservice.exception.AuthorizeException;
import ru.netology.cloudservice.model.AuthorizeData;
import ru.netology.cloudservice.model.Login;
import ru.netology.cloudservice.repository.FileRepository;
import ru.netology.cloudservice.repository.UserRepository;
import ru.netology.cloudservice.service.UserService;

import java.util.List;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CloudserviceApplicationTests {
    private final String filename = "filename";
    private String login = "ivan@mail.com";
    private final String password = "ivan";
    private final String token = "token";
    private final User user = new User(1L,login, password, token);

    private final File fileOne = new File(1,"file content".getBytes(), 12L, filename, user);
    private final File fileTwo = new File(2,"file content two".getBytes(), 16L, "new" + filename, user);

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    UserRepository userRepository;

    @Autowired
    FileRepository fileRepository;

    @Autowired
    UserService userService;

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
    void contextDatabase() {
        Assertions.assertTrue(database.isRunning());
    }

    @Test
    void testGetUserByLoginAndPassword() {
        //given
        User expected = user;

        // when:
        User actual = userRepository.findByLoginAndPassword("ivan@mail.com", "ivan").get();

        // then:
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void testGetUserByAuthToken() {
        //given

        User expected = user;

        // when:
        User actual = userRepository.findUserByAuthToken("token").get();

        // then:
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void testGetFileByNameAndUser() {
        //given
        File expected = fileOne;

        // when:
        File actual = fileRepository.findFileByNameAndUser(filename, user).get();

        // then:
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void testGetFile() {
        //given
        List<File> expected = List.of(fileOne, fileTwo);

        // when:
        List<File> actual = fileRepository.findAllFilesByUser(user, PageRequest.of(0, 2));

        // then:
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void testUserServiceLoginOK() {
        //given
        AuthorizeData authorizeData = new AuthorizeData("petr@mail.com", "petr");
        Login expected = new Login("token2");

        // when:
        Login actual = userService.login(authorizeData);

        // then:
        Assertions.assertSame(expected.getClass(), actual.getClass());
        Assertions.assertNotNull(actual.getAuthToken());
        Assertions.assertNotEquals(0, actual.getAuthToken().length());
    }

    @Test
    void testUserServiceLoginAuthorizeException() {
        //given
        User userWithoutToken = new User(login, Crypter.encrypt(password));
        login = "petr@mail.com";
        userRepository.save(userWithoutToken);

        // then:
        Assertions.assertThrows(AuthorizeException.class, () -> userService.login(new AuthorizeData(login, password)));
    }
}

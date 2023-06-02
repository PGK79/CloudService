package ru.netology.cloudservice;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.data.domain.PageRequest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.web.multipart.MultipartFile;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.netology.cloudservice.entity.File;
import ru.netology.cloudservice.entity.User;
import ru.netology.cloudservice.exception.AuthorizeException;
import ru.netology.cloudservice.exception.RepositoryException;
import ru.netology.cloudservice.model.AuthorizeData;
import ru.netology.cloudservice.model.Login;
import ru.netology.cloudservice.repository.FileRepository;
import ru.netology.cloudservice.repository.UserRepository;
import ru.netology.cloudservice.service.FileService;
import ru.netology.cloudservice.service.UserService;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

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

    @Autowired
    FileService fileService;

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
       AuthorizeData authorizeData = new AuthorizeData("petr@mail.com", "ivan");

        // then:
        Assertions.assertThrows(AuthorizeException.class, () -> userService.login(authorizeData));
    }

    @Test
    void testLogout(){
        // given:
        Optional<User> optionalUser = userRepository.findUserByAuthToken("token");
        String bearerToken = "Bearer " + token;
        User expected = optionalUser.get();

        // when:
        userService.logout(bearerToken);
        User actual = optionalUser.get();

        // then:
        Assertions.assertEquals(expected, actual);
    }
    @Test
    void testUploadFile() throws IOException {
        // given
        String bearerToken = "Bearer " + token;
        MultipartFile multipartFile = new MockMultipartFile("file", "filename zero",
                "text/plain", "file content".getBytes());

        // then:
        Assertions.assertTrue(fileService.uploadFile(bearerToken, filename + " zero", multipartFile));
    }


    @Test
    void testUploadFileRepositoryException() throws IOException {
        // given// given:
        String bearerToken = "Bearer " + token;
        MultipartFile multipartFile = Mockito.mock(MultipartFile.class);

        // then:
        Assertions.assertThrows(RepositoryException.class, () -> fileService.uploadFile(bearerToken, filename,
                multipartFile));
    }
}

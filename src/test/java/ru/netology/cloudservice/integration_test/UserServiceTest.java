package ru.netology.cloudservice.integration_test;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.netology.cloudservice.entity.User;
import ru.netology.cloudservice.exception.AuthorizeException;
import ru.netology.cloudservice.exception.TokenException;
import ru.netology.cloudservice.model.AuthorizeData;
import ru.netology.cloudservice.model.Login;
import ru.netology.cloudservice.repository.UserRepository;
import ru.netology.cloudservice.service.UserService;

import java.util.Optional;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserServiceTest {
    private final String login = "petr@mail.com";
    private final String password = "petr";
    private final String invalidPassword = "password";
    private final String invalidToken = "Bearer " + " anotherToken";
    private final String token = "token2";
    private final String bearerToken = "Bearer " + token;

    @Autowired
    UserRepository userRepository;

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
    public void testUserServiceLoginOK() {
        //given
        AuthorizeData authorizeData = new AuthorizeData(login, password);
        Login expected = new Login(token);

        // when:
        Login actual = userService.login(authorizeData);

        // then:
        Assertions.assertSame(expected.getClass(), actual.getClass());
        Assertions.assertNotNull(actual.getAuthToken());
        Assertions.assertNotEquals(0, actual.getAuthToken().length());
    }

    @Test
    public void testUserServiceLoginAuthorizeException() {
        //given
        AuthorizeData authorizeData = new AuthorizeData(login, invalidPassword);

        // then:
        Assertions.assertThrows(AuthorizeException.class, () -> userService.login(authorizeData));
    }

    @Test
    public void testUserServiceLogout() {
        // given:
        Optional<User> optionalUser = userRepository.findUserByAuthToken(token);
        User expected = optionalUser.get();

        // when:
        userService.logout(bearerToken);
        User actual = optionalUser.get();

        // then:
        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void testUserServiceLogoutTokenException() {
        // then:
        Assertions.assertThrows(TokenException.class, () -> userService.logout(invalidToken));
    }
}

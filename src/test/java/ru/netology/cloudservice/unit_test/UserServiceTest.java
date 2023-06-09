package ru.netology.cloudservice.unit_test;

import org.junit.jupiter.api.*;
import org.mockito.Mockito;
import ru.netology.cloudservice.cryptograph.Crypter;
import ru.netology.cloudservice.entity.User;
import ru.netology.cloudservice.exception.AuthorizeException;
import ru.netology.cloudservice.model.AuthorizeData;
import ru.netology.cloudservice.model.Login;
import ru.netology.cloudservice.repository.UserRepository;
import ru.netology.cloudservice.service.UserService;

import java.util.Optional;

public class UserServiceTest {
    static UserService sut;
    UserRepository userRepository = Mockito.mock(UserRepository.class);
    private String login = "ivan@mail.com";
    private final String password = "ivan";
    private final String authToken = "token";
    private final String cryptPassword = Crypter.encrypt(password);
    private Optional<User> optionalUser = Optional.of(new User(login, cryptPassword, authToken));

    @BeforeAll
    public static void startedAll() {
        System.out.println("Начало тестов пользовательского сервиса");
    }

    @BeforeEach
    public void InitAndStart() {
        System.out.println("Старт теста");
        sut = new UserService(userRepository);
    }

    @AfterAll
    public static void finishAll() {
        System.out.println("Все тесты пользовательского сервиса завершены");
    }

    @AfterEach
    public void finished() {
        System.out.println("Тест завершен");
        sut = null;
    }

    @Test
    public void testLoginOk() {
        // given:
        AuthorizeData authorizeData = new AuthorizeData(login, password);
        Mockito.when(userRepository.findByLoginAndPassword(login, cryptPassword)).thenReturn(optionalUser);
        Login expected = new Login(authToken);

        // when:
        Login actual = sut.login(authorizeData);

        // then:
        Assertions.assertSame(expected.getClass(), actual.getClass());
        Assertions.assertNotNull(actual.getAuthToken());
        Assertions.assertNotEquals(0, actual.getAuthToken().length());
    }

    @Test
    public void testLoginAuthorizeException() {
        // given:
        login = "ivan@mail.ru";
        optionalUser = Optional.empty();
        Mockito.when(userRepository.findByLoginAndPassword(login, password)).thenReturn(optionalUser);

        // then:
        Assertions.assertThrows(AuthorizeException.class, () -> sut.login(new AuthorizeData(login, password)));
    }

    @Test
    public void testLogout() {
        // given:
        Optional<User> optionalUser = Optional.of(new User(login, cryptPassword, authToken));
        String bearerToken = "Bearer " + authToken;
        Mockito.when(userRepository.findUserByAuthToken(bearerToken.split(" ")[1])).thenReturn(optionalUser);
        User expected = Optional.of(new User(login, cryptPassword, null)).get();

        // when:
        sut.logout(bearerToken);
        User actual = optionalUser.get();

        // then:
        Assertions.assertEquals(expected, actual);
    }
}
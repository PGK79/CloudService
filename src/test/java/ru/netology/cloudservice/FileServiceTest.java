package ru.netology.cloudservice;

import org.junit.jupiter.api.*;
import org.mockito.Mockito;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.multipart.MultipartFile;
import ru.netology.cloudservice.cryptograph.Crypter;
import ru.netology.cloudservice.entity.File;
import ru.netology.cloudservice.entity.User;
import ru.netology.cloudservice.exception.InputDataException;
import ru.netology.cloudservice.exception.RepositoryException;
import ru.netology.cloudservice.exception.TokenException;
import ru.netology.cloudservice.model.FileData;
import ru.netology.cloudservice.repository.FileRepository;
import ru.netology.cloudservice.repository.UserRepository;
import ru.netology.cloudservice.service.FileService;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class FileServiceTest {
    static FileService sut;
    UserRepository userRepository = Mockito.mock(UserRepository.class);
    FileRepository fileRepository = Mockito.mock(FileRepository.class);

    private final String login = "ivan@mail.com";
    private final String password = "ivan";
    private final String authToken = "token";
    private final String bearerToken = "Bearer " + authToken;
    private final String cryptPassword = Crypter.encrypt(password);
    private String filename = "filename";
    private final User user = new User(login, cryptPassword, authToken);
    private final Optional<User> optionalUser = Optional.of(user);
    private final MultipartFile multipartFile = Mockito.mock(MultipartFile.class);
    private final File file = new File(filename.getBytes(), 8, filename, user);
    private final Optional<File> optionalFile = Optional.of(file);
    private int limit = 3;

    @BeforeAll
    public static void startedAll() {
        System.out.println("Начало тестов файлового сервиса");
    }

    @BeforeEach
    public void InitAndStart() {
        System.out.println("Старт теста");
        sut = new FileService(fileRepository, userRepository);
    }

    @AfterAll
    public static void finishAll() {
        System.out.println("Все тесты файлового сервиса завершены");
    }

    @AfterEach
    public void finished() {
        System.out.println("Тест завершен");
        sut = null;
    }

    @Test
    public void testUploadFile() throws IOException {
        // given:
        Mockito.when(userRepository.findUserByAuthToken(bearerToken.split(" ")[1])).thenReturn(optionalUser);

        // then:
        Assertions.assertTrue(sut.uploadFile(bearerToken, filename, multipartFile));
    }

    @Test
    public void testUploadFileTokenException() {
        // given:
        Mockito.when(userRepository.findUserByAuthToken(bearerToken)).thenReturn(optionalUser);

        // then:
        Assertions.assertThrows(TokenException.class, () -> sut.uploadFile(bearerToken, filename, multipartFile));
    }

    @Test
    public void testUploadFileInputDataExceptionMultipartFileIsNull() {
        // given:
        Mockito.when(userRepository.findUserByAuthToken(bearerToken.split(" ")[1])).thenReturn(optionalUser);

        // then:
        Assertions.assertThrows(InputDataException.class, () -> sut.uploadFile(bearerToken, filename, null));
    }

    @Test
    public void testUploadFileInputDataExceptionFilenameIsEmpty() {
        // given:
        filename = "";
        Mockito.when(userRepository.findUserByAuthToken(bearerToken.split(" ")[1])).thenReturn(optionalUser);

        // then:
        Assertions.assertThrows(InputDataException.class, () -> sut.uploadFile(bearerToken, filename, multipartFile));
    }

    @Test
    public void testDeleteFile() {
        // given:
        Mockito.when(userRepository.findUserByAuthToken(authToken)).thenReturn(optionalUser);
        Mockito.when(fileRepository.findFileByNameAndUser(filename, user)).thenReturn(optionalFile);

        // then:
        Assertions.assertTrue(sut.deleteFile(bearerToken, filename));
    }

    @Test
    public void testDeleteFileTokenException() {
        // given:
        Mockito.when(userRepository.findUserByAuthToken(authToken)).thenThrow(new TokenException("Ошибка"));

        // then:
        Assertions.assertThrows(TokenException.class, () -> sut.deleteFile(bearerToken, filename));
    }

    @Test
    public void testDeleteFileInputDataException() {
        // given:
        filename = "";
        Mockito.when(userRepository.findUserByAuthToken(authToken)).thenReturn(optionalUser);
        Mockito.when(fileRepository.findFileByNameAndUser(filename, user)).thenReturn(optionalFile);

        // then:
        Assertions.assertThrows(InputDataException.class, () -> sut.deleteFile(bearerToken, filename));
    }

    @Test
    public void testDeleteFileRepositoryException() {
        // given:
        Mockito.when(userRepository.findUserByAuthToken(authToken)).thenReturn(optionalUser);
        Mockito.when(fileRepository.findFileByNameAndUser(filename, user)).thenThrow(new RepositoryException("Ошибка"));

        // then:
        Assertions.assertThrows(RepositoryException.class, () -> sut.deleteFile(bearerToken, filename));
    }

    @Test
    public void testGetFile() {
        // given:
        Mockito.when(userRepository.findUserByAuthToken(authToken)).thenReturn(optionalUser);
        Mockito.when(fileRepository.findFileByNameAndUser(filename, user)).thenReturn(optionalFile);

        byte[] expected = file.getContent();

        // when:
        byte[] actual = sut.getFile(bearerToken, filename);

        // then:
        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void testGetFileTokenException() {
        // given:
        Mockito.when(userRepository.findUserByAuthToken(authToken)).thenThrow(new TokenException("Ошибка"));

        // then:
        Assertions.assertThrows(TokenException.class, () -> sut.getFile(bearerToken, filename));
    }

    @Test
    public void testGetFileInputDataException() {
        // given:
        filename = "";
        Mockito.when(userRepository.findUserByAuthToken(authToken)).thenReturn(optionalUser);
        Mockito.when(fileRepository.findFileByNameAndUser(filename, user)).thenReturn(optionalFile);

        // then:
        Assertions.assertThrows(InputDataException.class, () -> sut.getFile(bearerToken, filename));
    }

    @Test
    public void testGetFileRepositoryException() {
        // given:
        Mockito.when(userRepository.findUserByAuthToken(authToken)).thenReturn(optionalUser);
        Mockito.when(fileRepository.findFileByNameAndUser(filename, user)).thenThrow(new RepositoryException("Ошибка"));

        // then:
        Assertions.assertThrows(RepositoryException.class, () -> sut.getFile(bearerToken, filename));
    }

    @Test
    public void testRenameFile() {
        // given:
        Mockito.when(userRepository.findUserByAuthToken(authToken)).thenReturn(optionalUser);
        Mockito.when(fileRepository.findFileByNameAndUser(filename, user)).thenReturn(optionalFile);

        // then:
        Assertions.assertTrue(sut.renameFile(bearerToken, filename, "newFileName"));
    }

    @Test
    public void testRenameFileTokenException() {
        // given:
        Mockito.when(userRepository.findUserByAuthToken(authToken)).thenThrow(new TokenException("Ошибка"));

        // then:
        Assertions.assertThrows(TokenException.class, () -> sut.renameFile(bearerToken, filename, "newFileName"));
    }

    @Test
    public void testRenameFileInputDataException() {
        // given:
        filename = "";
        Mockito.when(userRepository.findUserByAuthToken(authToken)).thenReturn(optionalUser);
        Mockito.when(fileRepository.findFileByNameAndUser(filename, user)).thenReturn(optionalFile);

        // then:
        Assertions.assertThrows(InputDataException.class, () -> sut.renameFile(bearerToken, filename, "newFileName"));
    }

    @Test
    public void testRenameFileRepositoryException() {
        // given:
        Mockito.when(userRepository.findUserByAuthToken(authToken)).thenReturn(optionalUser);
        Mockito.when(fileRepository.findFileByNameAndUser(filename, user)).thenThrow(new RepositoryException("Ошибка"));

        // then:
        Assertions.assertThrows(RepositoryException.class, () -> sut.renameFile(bearerToken, filename, "newFileName"));
    }

    @Test
    public void testGetList() {
        // given:
        List<File> files = List.of(file);
        FileData fileData = new FileData(filename, 8);

        Mockito.when(userRepository.findUserByAuthToken(authToken)).thenReturn(optionalUser);
        Mockito.when(fileRepository.findAllFilesByUser(user, PageRequest.of(0, limit))).thenReturn(files);

        // when:
        List<FileData> expected = List.of(fileData);

        // then:
        List<FileData> actual = sut.getList(bearerToken, limit);

        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void testGetListTokenException() {
        Mockito.when(userRepository.findUserByAuthToken(authToken)).thenThrow(new TokenException("Ошибка"));

        // then:
        Assertions.assertThrows(TokenException.class, () -> sut.getList(bearerToken, limit));
    }

    @Test
    public void testGetListInputDataException() {
        // given:
        Mockito.when(userRepository.findUserByAuthToken(authToken)).thenReturn(optionalUser);
        Mockito.when(fileRepository.findAllFilesByUser(user, PageRequest.of(0, limit))).thenThrow(new InputDataException("Ошибка"));

        // then:
        Assertions.assertThrows(InputDataException.class, () -> sut.getList(bearerToken, -1));
    }

    @Test
    public void testGetListRepositoryException() {
        // given:
        Mockito.when(userRepository.findUserByAuthToken(authToken)).thenReturn(optionalUser);
        Mockito.when(fileRepository.findAllFilesByUser(user, PageRequest.of(0, limit))).thenThrow(new RepositoryException("Ошибка"));

        // then:
        Assertions.assertThrows(RepositoryException.class, () -> sut.getList(bearerToken, limit));
    }






    @Test
    public void testGiveFileFromRepository() {
        // given:
        File expected = file;
        Optional<File> optionalFile = Optional.of(expected);

        Mockito.when(userRepository.findUserByAuthToken(bearerToken.split(" ")[1])).thenReturn(optionalUser);
        Mockito.when(fileRepository.findFileByNameAndUser(filename, user)).thenReturn(optionalFile);

        // when:
        File actual = sut.giveFileFromRepository(filename, bearerToken);

        // then:
        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void testGiveFileFromRepositoryRepositoryException() {
        // given:
        Mockito.when(userRepository.findUserByAuthToken(bearerToken.split(" ")[1])).thenReturn(optionalUser);
        Mockito.when(fileRepository.findFileByNameAndUser(filename, user)).thenThrow(new RepositoryException("Ошибка"));

        // then:
        Assertions.assertThrows(RepositoryException.class, () -> sut.giveFileFromRepository(filename, bearerToken));
    }

    @Test
    public void testSaveFileInRepository() {
        Assertions.assertTrue(sut.saveFileInRepository(file));
    }

    @Test
    public void testSaveFileInRepositoryRepositoryException() {
        // given:
        Mockito.when(fileRepository.saveAndFlush(file)).thenThrow(new RepositoryException("Ошибка"));

        // then:
        Assertions.assertThrows(RepositoryException.class, () -> sut.saveFileInRepository(file));
    }
}
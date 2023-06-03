package ru.netology.cloudservice.integration_test;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.web.multipart.MultipartFile;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.netology.cloudservice.entity.File;
import ru.netology.cloudservice.entity.User;
import ru.netology.cloudservice.exception.InputDataException;
import ru.netology.cloudservice.exception.RepositoryException;
import ru.netology.cloudservice.exception.TokenException;
import ru.netology.cloudservice.model.FileData;
import ru.netology.cloudservice.service.FileService;

import java.io.IOException;
import java.util.List;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class FileServiceTest {
    private String filename = "filename";
    private String login = "ivan@mail.com";
    private final String password = "ivan";
    private final String token = "token";
    private final String bearerToken = "Bearer " + token;
    private final String invalidToken = "Bearer " + " anotherToken";
    private final User user = new User(1L, login, password, token);
    private final File fileOne = new File(1, "file content".getBytes(), 12L, filename, user);
    private final File fileTwo = new File(2, "new file content".getBytes(), 16L, "new" + filename, user);

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
    public void testUploadFile() throws IOException {
        // given
        MultipartFile multipartFile = new MockMultipartFile("file", "filename zero",
                "text/plain", "file content".getBytes());

        // then:
        Assertions.assertTrue(fileService.uploadFile(bearerToken, filename + " zero", multipartFile));
    }

    @Test
    public void testUploadFileTokenException() {
        // given:
        MultipartFile multipartFile = Mockito.mock(MultipartFile.class);

        // then:
        Assertions.assertThrows(TokenException.class, () -> fileService.uploadFile(invalidToken, filename, multipartFile));
    }

    @Test
    public void testUploadFileInputDataExceptionMultipartFileIsNull() {
        // then:
        Assertions.assertThrows(InputDataException.class, () -> fileService.uploadFile(bearerToken, filename, null));
    }

    @Test
    public void testUploadFileInputDataExceptionFilenameIsEmpty() {
        // given
        filename = "";
        MultipartFile multipartFile = new MockMultipartFile("file", filename, "text/plain",
                "file content".getBytes());

        // then:
        Assertions.assertThrows(InputDataException.class, () -> fileService.uploadFile(bearerToken, filename, multipartFile));
    }

    @Test
    public void testUploadFileRepositoryException() throws IOException {
        // given
        MultipartFile multipartFile = Mockito.mock(MultipartFile.class);

        // then:
        Assertions.assertThrows(RepositoryException.class, () -> fileService.uploadFile(bearerToken, filename,
                multipartFile));
    }

    @Test
    public void testDeleteFile() {
        // given
        filename = "removable";

        // then:
        Assertions.assertTrue(fileService.deleteFile(bearerToken, filename));
    }

    @Test
    public void testDeleteFileTokenException() {
        // then:
        Assertions.assertThrows(TokenException.class, () -> fileService.deleteFile(invalidToken, filename));
    }

    @Test
    public void testDeleteFileInputDataException() {
        // given:
        filename = "";

        // then:
        Assertions.assertThrows(InputDataException.class, () -> fileService.deleteFile(bearerToken, filename));
    }

    @Test
    public void testDeleteFileRepositoryException() {
        // given:
        filename = "anotherFilename";

        // then:
        Assertions.assertThrows(RepositoryException.class, () -> fileService.deleteFile(bearerToken, filename));
    }

    @Test
    public void testFileServiceGetFile() {
        // given:
        byte[] expected = fileOne.getContent();

        // when:
        byte[] actual = fileService.getFile(bearerToken, filename);

        // then:
        Assertions.assertArrayEquals(expected, actual);
    }

    @Test
    public void testFileServiceGetFileTokenException() {
        // then:
        Assertions.assertThrows(TokenException.class, () -> fileService.getFile(invalidToken, filename));
    }

    @Test
    public void testFileServiceGetFileInputDataException() {
        // given:
        filename = "";

        // then:
        Assertions.assertThrows(InputDataException.class, () -> fileService.getFile(bearerToken, filename));
    }

    @Test
    public void testFileServiceGetFileRepositoryException() {
        // given:
        filename = "anotherFilename";

        // then:
        Assertions.assertThrows(RepositoryException.class, () -> fileService.getFile(bearerToken, filename));
    }

    @Test
    public void testRenameFile() {
        //given
        boolean expected = true;

        // when:
        boolean actual = fileService.renameFile(bearerToken, "renamed", "newname");

        // then:
        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void testRenameFileTokenException() {
        // then:
        Assertions.assertThrows(TokenException.class, () -> fileService.renameFile(invalidToken, filename,
                "newFileName"));
    }

    @Test
    public void testRenameFileInputDataException() {
        // given:
        filename = "";

        // then:
        Assertions.assertThrows(InputDataException.class, () -> fileService.renameFile(bearerToken, filename,
                "newFileName"));
    }

    @Test
    public void testRenameFileRepositoryException() {
        // then:
        Assertions.assertThrows(RepositoryException.class, () -> fileService.renameFile(bearerToken, filename,
                "newFileName"));
    }

    @Test
    public void testGetList() {
        // given:
        Integer limit = 1;
        List<FileData> expected = List.of(new FileData(filename, 12));

        // when:
        List<FileData> actual = fileService.getList(bearerToken, limit);

        // then:
        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void testGetListTokenException() {
        // given:
        Integer limit = 1;

        // then:
        Assertions.assertThrows(TokenException.class, () -> fileService.getList(invalidToken, limit));
    }

    @Test
    public void testGetListInputDataException() {
        // given:
        Integer limit = -1;

        // then:
        Assertions.assertThrows(InputDataException.class, () -> fileService.getList(bearerToken, limit));
    }
}


package ru.netology.cloudservice;

import org.mockito.Mockito;
import ru.netology.cloudservice.repository.FileRepository;
import ru.netology.cloudservice.service.FileService;

public class FileServiceTest {
    static FileService sut;
    FileRepository fileRepository = Mockito.mock(FileRepository.class);

}

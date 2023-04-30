package ru.netology.cloudservice.service;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.netology.cloudservice.entity.FileEntity;
import ru.netology.cloudservice.entity.User;
import ru.netology.cloudservice.exception.InputDataException;
import ru.netology.cloudservice.exception.RepositoryException;
import ru.netology.cloudservice.exception.TokenException;
import ru.netology.cloudservice.model.File;
import ru.netology.cloudservice.model.FileData;
import ru.netology.cloudservice.repository.FileRepository;
import ru.netology.cloudservice.repository.UserRepository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class FileService {
    private final FileRepository fileRepository;
    private final UserRepository userRepository;

    public void uploadFile(String authToken, String filename, File file) throws IOException {
        checkToken(authToken);
        if (file == null || filename.isEmpty()) {
            throw new InputDataException("Ошибка при передаче файла");
        }
        FileEntity inputFileEntity = new FileEntity(file.getFile().getBytes(), file.getFile().getSize(), filename,
                getUserByToken(authToken));
        saveFileInRepository(inputFileEntity);
    }

    public void deleteFile(String authToken, String filename) {
        checkToken(authToken);
        checkFile(filename);
        fileRepository.delete(giveFileFromRepository(filename, authToken));
    }

    public byte[] getFile(String authToken, String filename) {
        checkToken(authToken);
        checkFile(filename);
        return giveFileFromRepository(filename, authToken).getContent();
    }

    public void renameFile(String authToken, String filename, String name) {
        checkToken(authToken);
        checkFile(filename);
        FileEntity fileEntity = giveFileFromRepository(filename, authToken);
        fileEntity.setName(name);
        saveFileInRepository(fileEntity);
    }

    public List<FileData> getList(String authToken, Integer limit){
        checkToken(authToken);
        if (limit < 0){
            throw new InputDataException("Значение лимита ошибочно");
        }
        try {
        List<FileEntity> list = fileRepository.findAllFilesByUser(getUserByToken(authToken), PageRequest.of(0, limit));
        List<FileData> fileDataList = new ArrayList<>();
        for(FileEntity fileEntity : list){
            FileData fileData = new FileData(fileEntity.getName(), fileEntity.getSize());
            fileDataList.add(fileData);
        }
        return fileDataList;
        } catch (RuntimeException e) {
            throw new RepositoryException("Не возможно вернуть список файлов");
        }
    }
    public User getUserByToken(String token) {
        Optional<User> userResult = userRepository.findUserByAuthToken(token.split(" ")[1]);
        if (userResult.isPresent()) {
            return userResult.get();
        } else throw new TokenException("Пользователь по переданному токену не найден");
    }

    public void checkToken(String token) {
        if (token.equals(null)) {
            throw new TokenException("Токен ошибочный");
        }
    }

    public void checkFile(String filename) {
        if (filename.isEmpty()) {
            throw new InputDataException("Ошибка при передаче файла");
        }
    }

    public FileEntity giveFileFromRepository(String filename, String authToken) {
        Optional<FileEntity> resultFile = fileRepository.findFileByNameAndUser(filename, getUserByToken(authToken));
        if (resultFile.isPresent()) {
            return resultFile.get();
        } else {
            throw new RepositoryException("Файл в репозитории не найден");
        }
    }

    public void saveFileInRepository(FileEntity fileEntity){
        try {
            fileRepository.saveAndFlush(fileEntity);
        } catch (RuntimeException e) {
            throw new RepositoryException("Ошибка сохранения файла в репозиторий");
        }
    }
}
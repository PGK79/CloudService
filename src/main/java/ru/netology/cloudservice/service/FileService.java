package ru.netology.cloudservice.service;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.netology.cloudservice.entity.File;
import ru.netology.cloudservice.entity.UserEntity;
import ru.netology.cloudservice.exception.InputDataException;
import ru.netology.cloudservice.exception.RepositoryException;
import ru.netology.cloudservice.exception.TokenException;
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

    public void uploadFile(String authToken, String filename, MultipartFile file) throws IOException {
        checkToken(authToken);
        if (file == null || filename.isEmpty()) {
            throw new InputDataException("Ошибка при передаче файла");
        }
        File fileEntity = new File();
        fileEntity.setUser(getUserByToken(authToken));
        fileEntity.setSize(file.getSize());
        fileEntity.setName(filename);
        fileEntity.setContent(file.getBytes());
        saveFileInRepository(fileEntity);
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
        File file = giveFileFromRepository(filename, authToken);
        file.setName(name);
        saveFileInRepository(file);
    }

    public List<FileData> getList(String authToken, Integer limit){
        checkToken(authToken);
        if (limit < 0){
            throw new InputDataException("Значение лимита ошибочно");
        }
        try {
        List<File> list = fileRepository.findAllFilesByUser(getUserByToken(authToken), PageRequest.of(0, limit));
        List<FileData> fileDataList = new ArrayList<>();
        for(File file : list){
            FileData fileData = new FileData(file.getName(), file.getSize());
            fileDataList.add(fileData);
        }
        return fileDataList;
        } catch (RuntimeException e) {
            throw new RepositoryException("Не возможно вернуть список файлов");
        }
    }

    public UserEntity getUserByToken(String token) {
        Optional<UserEntity> userResult = userRepository.findUserByAuthToken(token.split(" ")[1]);
        if (userResult.isPresent()) {
            return userResult.get();
        } else throw new TokenException("Пользователь по переданному токену не найден");
    }

    public void checkToken(String token) {
        if (token == null) {
            throw new TokenException("Токен ошибочный");
        }
    }

    public void checkFile(String filename) {
        if (filename.isEmpty()) {
            throw new InputDataException("Ошибка при передаче файла");
        }
    }

    public File giveFileFromRepository(String filename, String authToken) {
        Optional<File> resultFile = fileRepository.findFileByNameAndUser(filename, getUserByToken(authToken));
        if (resultFile.isPresent()) {
            return resultFile.get();
        } else {
            throw new RepositoryException("Файл в репозитории не найден");
        }
    }

    public void saveFileInRepository(File file){
        try {
            fileRepository.saveAndFlush(file);
        } catch (RuntimeException e) {
            throw new RepositoryException("Ошибка сохранения файла в репозиторий");
        }
    }
}
package ru.netology.cloudservice.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.netology.cloudservice.cryptograph.Crypter;
import ru.netology.cloudservice.entity.UserEntity;
import ru.netology.cloudservice.exception.AuthorizeException;
import ru.netology.cloudservice.exception.TokenException;
import ru.netology.cloudservice.model.AuthorizeData;
import ru.netology.cloudservice.model.Login;
import ru.netology.cloudservice.repository.UserRepository;

import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public Login login(AuthorizeData authorizeData) {
        Optional<UserEntity> userResult = userRepository.findByLoginAndPassword(authorizeData.getLogin(),
                Crypter.encrypt(authorizeData.getPassword()));
        String token;
        if (userResult.isPresent()) {
            UserEntity user = userResult.get();
            token = UUID.randomUUID().toString();
            user.setAuthToken(token);
            userRepository.saveAndFlush(user);
            return new Login(token);
        } else {
            throw new AuthorizeException("Логин и/или пароль не верны");
        }
    }

    public void logout(String authToken) {
        Optional<UserEntity> userResult = userRepository.findUserByAuthToken(authToken.split(" ")[1]);
        if (userResult.isPresent()) {
            UserEntity user = userResult.get();
            user.setAuthToken(null);
            userRepository.saveAndFlush(user);
        } else {
            throw new TokenException("Токен ошибочен");
        }
    }
}
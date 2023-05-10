package ru.netology.cloudservice.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.netology.cloudservice.cryptograph.Crypter;
import ru.netology.cloudservice.entity.User;
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
        Optional<User> userResult = userRepository.findByLoginAndPassword(authorizeData.getLogin(),
                Crypter.encrypt(authorizeData.getPassword()));
        if (userResult.isPresent()) {
            User user = userResult.get();
            user.setAuthToken(UUID.randomUUID().toString());
            userRepository.saveAndFlush(user);
            return new Login(user.getAuthToken());
        } else {
            throw new AuthorizeException("Логин и/или пароль не верны");
        }
    }

    public void logout(String authToken) {
        Optional<User> userResult = userRepository.findUserByAuthToken(authToken.split(" ")[1]);
        if (userResult.isPresent()) {
            User user = userResult.get();
            user.setAuthToken(null);
            userRepository.saveAndFlush(user);
        } else {
            throw new TokenException("Токен ошибочен");
        }
    }
}
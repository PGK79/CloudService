package ru.netology.cloudservice.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.netology.cloudservice.entity.User;
import ru.netology.cloudservice.exception.AuthorizeException;
import ru.netology.cloudservice.exception.TokenException;
import ru.netology.cloudservice.model.Login;
import ru.netology.cloudservice.repository.UserRepository;

import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public Login login(String login, String password) {
        Optional<User> userResult = userRepository.findByLoginAndPassword(login, password);
        String token;
        if (userResult.isPresent()) {
            User user = userResult.get();
            token = UUID.randomUUID().toString();
            user.setAuthToken(token);
            userRepository.saveAndFlush(user);
            return new Login(token);
        } else {
            throw new AuthorizeException("Логин и/или пароль не верны");
        }
    }

}

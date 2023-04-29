package ru.netology.cloudservice.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import ru.netology.cloudservice.model.AuthorizeData;
import ru.netology.cloudservice.model.Login;
import ru.netology.cloudservice.service.FileService;
import ru.netology.cloudservice.service.UserService;

@RestController
public class CloudController {
    private final UserService userService;
    private final FileService fileService;

    public CloudController(UserService userService, FileService fileService) {
        this.userService = userService;
        this.fileService = fileService;
    }

    @PostMapping("/login")
    public Login login(@RequestBody AuthorizeData authorizeData) {
        return userService.login(authorizeData.getLogin(), authorizeData.getPassword());
    }

    @PostMapping("/logout")
    public void logout(@RequestHeader("auth-token") String authToken) {
        userService.logout(authToken);
    }

}





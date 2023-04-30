package ru.netology.cloudservice.controller;

import org.springframework.web.bind.annotation.*;
import ru.netology.cloudservice.model.AuthorizeData;
import ru.netology.cloudservice.model.File;
import ru.netology.cloudservice.model.FileData;
import ru.netology.cloudservice.model.Login;
import ru.netology.cloudservice.service.FileService;
import ru.netology.cloudservice.service.UserService;

import java.io.IOException;
import java.util.List;

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

    @PostMapping("/file")
    public void uploadFile(@RequestHeader("auth-token") String authToken, @ModelAttribute File file,
                           @RequestParam String filename) throws IOException {
        fileService.uploadFile(authToken, filename, file);
    }

    @DeleteMapping("/file")
    public void deleteFile(@RequestHeader("auth-token") String authToken, @RequestParam("filename") String filename) {
        fileService.deleteFile(authToken, filename);
    }
    @GetMapping("/file")
    public byte[] getFile(@RequestHeader("auth-token") String authToken, @RequestParam String filename) {
        return fileService.getFile(authToken, filename);
    }

    @PutMapping("/file")
    public void putFile(@RequestHeader("auth-token") String authToken, @RequestParam("filename") String filename,
                        @RequestBody FileData newFileName) {
        fileService.renameFile(authToken, filename, newFileName.getFilename());
    }

    @GetMapping("/list")
    public List<FileData> getList(@RequestHeader("auth-token") String authToken, @RequestParam("limit") Integer limit) {
        return fileService.getList(authToken, limit);
    }
}





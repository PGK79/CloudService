package ru.netology.cloudservice.model;

import lombok.Data;

@Data
public class AuthorizeData {
    private String login;
    private String password;
}
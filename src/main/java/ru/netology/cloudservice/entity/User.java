package ru.netology.cloudservice.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@NoArgsConstructor
@Data
@Table(name = "users")
public class User {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private long id;

    @Column(name = "login")
    private String login;

    @Column(name = "password")
    private String password;

    @Column(name = "auth_token")
    private String authToken;

    public User(String login, String password, String authToken) {
        this.login = login;
        this.password = password;
        this.authToken = authToken;
    }
}
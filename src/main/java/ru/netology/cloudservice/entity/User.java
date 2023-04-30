package ru.netology.cloudservice.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

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

    @OneToMany(mappedBy = "user")
    private List<FileEntity> fileEntities;

    public User(String login, String password, String authToken, List<FileEntity> fileEntities) {
        this.login = login;
        this.password = password;
        this.authToken = authToken;
        this.fileEntities = fileEntities;
    }
}
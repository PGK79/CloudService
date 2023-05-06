package ru.netology.cloudservice.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@NoArgsConstructor
@Data
@Table(name = "files")
public class File {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "content")
    private byte[] content;

    @Column(name = "size")
    private long size;

    @Column(name = "name")
    private String name;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public File(byte[] content, long size, String name, User user) {
        this.content = content;
        this.size = size;
        this.name = name;
        this.user = user;
    }
}
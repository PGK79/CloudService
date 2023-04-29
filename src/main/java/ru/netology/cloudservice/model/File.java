package ru.netology.cloudservice.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;

@Data
@AllArgsConstructor
public final class File implements Serializable {
    private MultipartFile file;
    private String hash;
}
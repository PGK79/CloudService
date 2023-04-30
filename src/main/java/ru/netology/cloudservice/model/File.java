package ru.netology.cloudservice.model;


import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public final class File{
    private String hash;
    private byte[] file;
}
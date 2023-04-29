package ru.netology.cloudservice.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FileData{
    private String filename;
    private long size;
}

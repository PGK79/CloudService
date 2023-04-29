package ru.netology.cloudservice.exception;

import lombok.Data;

@Data
public class AuthorizeException extends RuntimeException {

    public AuthorizeException(String msg) {
        super(msg);
    }
}

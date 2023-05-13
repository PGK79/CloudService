package ru.netology.cloudservice.exception;

import lombok.Data;

@Data
public class TokenException extends RuntimeException {
    public TokenException(String msg) {
        super(msg);
    }
}
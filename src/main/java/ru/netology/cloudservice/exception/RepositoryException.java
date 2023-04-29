package ru.netology.cloudservice.exception;

public class RepositoryException extends RuntimeException{
    public RepositoryException(String msg) {
        super(msg);
    }
}
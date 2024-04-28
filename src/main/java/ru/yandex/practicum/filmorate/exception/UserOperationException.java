package ru.yandex.practicum.filmorate.exception;

public class UserOperationException extends RuntimeException {
    public UserOperationException(String message) {
        super(message);
    }
}

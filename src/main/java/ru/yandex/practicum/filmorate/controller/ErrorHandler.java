package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.UserOperationException;
import ru.yandex.practicum.filmorate.model.ErrorResponse;

@RestControllerAdvice
@Slf4j
public class ErrorHandler {

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFoundException(NotFoundException e) {
        log.warn(e.getMessage());
        ErrorResponse errorResponse = new ErrorResponse("Не найдено",
                e.getMessage());
        return errorResponse;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidationException(MethodArgumentNotValidException e) {
        log.warn(e.getMessage());
        ErrorResponse errorResponse = new ErrorResponse("Ошибка валидации",
                e.getMessage());
        return errorResponse;
    }

    @ExceptionHandler(UserOperationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleUserOperationException(UserOperationException e) {
        log.warn(e.getMessage());
        ErrorResponse errorResponse = new ErrorResponse("Ошибка выбора пользователя",
                e.getMessage());
        return errorResponse;
    }
}

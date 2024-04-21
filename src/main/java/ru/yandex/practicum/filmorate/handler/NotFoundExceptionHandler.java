package ru.yandex.practicum.filmorate.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.yandex.practicum.filmorate.exception.NotFoundException;


@ControllerAdvice
public class NotFoundExceptionHandler {
    private final Logger log = LoggerFactory.getLogger(NotFoundException.class);

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<String> handleException(NotFoundException e) {
        log.warn(e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .header("Content-Type", "application/json")
                .body("{}");
        // Временный костыль, не придумал чем заменить, Postman ожидает пустое тело
    }

}

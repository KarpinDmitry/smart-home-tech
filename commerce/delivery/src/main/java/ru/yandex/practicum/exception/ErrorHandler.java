package ru.yandex.practicum.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practicum.dto.ErrorResponse;

@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler(NoDeliveryFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(NoDeliveryFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                ErrorResponse.builder()
                        .httpStatus(HttpStatus.NOT_FOUND.name())
                        .userMessage("Не найдена доставка")
                        .message(e.getMessage())
                        .build()
        );
    }
}

package ru.yandex.practicum.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practicum.dto.ErrorResponse;

@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler(NoOrderFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(NoOrderFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                ErrorResponse.builder()
                        .httpStatus(HttpStatus.NOT_FOUND.name())
                        .userMessage("Заказ не найден")
                        .message(e.getMessage())
                        .build()
        );
    }

    @ExceptionHandler(NotEnoughInfoInOrderToCalculateException.class)
    public ResponseEntity<ErrorResponse> handleNotEnoughInfo(NotEnoughInfoInOrderToCalculateException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                ErrorResponse.builder()
                        .httpStatus(HttpStatus.BAD_REQUEST.name())
                        .userMessage("Недостаточно информации в заказе для расчёта")
                        .message(e.getMessage())
                        .build()
        );
    }
}

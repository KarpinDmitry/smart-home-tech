package ru.yandex.practicum.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practicum.dto.ErrorResponse;

@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler(NoOrderFoundException.class)
    public ResponseEntity<ErrorResponse> handleNoOrder(NoOrderFoundException e) {
        return badRequest("Не найден заказ", e.getMessage());
    }

    @ExceptionHandler(NotAuthorizedUserException.class)
    public ResponseEntity<ErrorResponse> handleUnauthorized(NotAuthorizedUserException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                ErrorResponse.builder()
                        .httpStatus(HttpStatus.UNAUTHORIZED.name())
                        .userMessage("Имя пользователя не должно быть пустым")
                        .message(e.getMessage())
                        .build()
        );
    }

    @ExceptionHandler(NoSpecifiedProductInWarehouseException.class)
    public ResponseEntity<ErrorResponse> handleNoProduct(NoSpecifiedProductInWarehouseException e) {
        return badRequest("Нет заказываемого товара на складе", e.getMessage());
    }

    private ResponseEntity<ErrorResponse> badRequest(String userMessage, String message) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                ErrorResponse.builder()
                        .httpStatus(HttpStatus.BAD_REQUEST.name())
                        .userMessage(userMessage)
                        .message(message)
                        .build()
        );
    }
}

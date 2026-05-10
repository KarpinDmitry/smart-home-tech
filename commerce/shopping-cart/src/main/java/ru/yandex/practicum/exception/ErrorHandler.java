package ru.yandex.practicum.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practicum.dto.ErrorResponse;

@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler(NotAuthorizedUserException.class)
    public ResponseEntity<ErrorResponse> handleNotAuthorized(NotAuthorizedUserException e) {
        ErrorResponse body = ErrorResponse.builder()
                .httpStatus(HttpStatus.UNAUTHORIZED.name())
                .userMessage("Имя пользователя не должно быть пустым")
                .message(e.getMessage())
                .build();
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(body);
    }

    @ExceptionHandler(NoProductsInShoppingCartException.class)
    public ResponseEntity<ErrorResponse> handleNoProducts(NoProductsInShoppingCartException e) {
        ErrorResponse body = ErrorResponse.builder()
                .httpStatus(HttpStatus.BAD_REQUEST.name())
                .userMessage("Нет искомых товаров в корзине")
                .message(e.getMessage())
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }
}

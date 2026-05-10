package ru.yandex.practicum.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practicum.dto.ErrorResponse;

@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler(SpecifiedProductAlreadyInWarehouseException.class)
    public ResponseEntity<ErrorResponse> handleAlreadyExists(SpecifiedProductAlreadyInWarehouseException e) {
        return badRequest("Товар с таким описанием уже зарегистрирован на складе", e.getMessage());
    }

    @ExceptionHandler(NoSpecifiedProductInWarehouseException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(NoSpecifiedProductInWarehouseException e) {
        return badRequest("Нет информации о товаре на складе", e.getMessage());
    }

    @ExceptionHandler(ProductInShoppingCartLowQuantityInWarehouse.class)
    public ResponseEntity<ErrorResponse> handleLowQuantity(ProductInShoppingCartLowQuantityInWarehouse e) {
        return badRequest("Товар из корзины не находится в требуемом количестве на складе", e.getMessage());
    }

    private ResponseEntity<ErrorResponse> badRequest(String userMessage, String message) {
        ErrorResponse body = ErrorResponse.builder()
                .httpStatus(HttpStatus.BAD_REQUEST.name())
                .userMessage(userMessage)
                .message(message)
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }
}

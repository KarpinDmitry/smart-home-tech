package ru.yandex.practicum.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ErrorResponse {
    private String httpStatus;
    private String userMessage;
    private String message;
}

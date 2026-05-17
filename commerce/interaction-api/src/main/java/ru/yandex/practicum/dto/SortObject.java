package ru.yandex.practicum.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SortObject {
    private String direction;
    private String nullHandling;
    private boolean ascending;
    private String property;
    private boolean ignoreCase;
}

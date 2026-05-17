package ru.yandex.practicum.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class PageProductDto {
    private long totalElements;
    private int totalPages;
    private boolean first;
    private boolean last;
    private int size;
    private int number;
    private int numberOfElements;
    private boolean empty;
    private List<SortObject> sort;
    private List<ProductDto> content;
}

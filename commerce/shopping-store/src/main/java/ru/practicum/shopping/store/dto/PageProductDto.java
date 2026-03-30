package ru.practicum.shopping.store.dto;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public record PageProductDto<T>(
        List<T> content,
        long totalElements,
        int totalPages,
        boolean first,
        boolean last,
        int size,
        int number,
        int numberOfElements,
        boolean empty,
        Pageable pageable,
        List<SortDto> sort
) {
    public static <T> PageProductDto<T> from(Page<T> page) {
        List<SortDto> sortDto = page.getSort().stream()
                .map(SortDto::from)
                .toList();

        return new PageProductDto<>(
                page.getContent(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isFirst(),
                page.isLast(),
                page.getSize(),
                page.getNumber(),
                page.getNumberOfElements(),
                page.isEmpty(),
                page.getPageable(),
                sortDto
        );
    }
}
package ru.practicum.interaction.api.dto.response;

import org.springframework.data.domain.Sort;

public record SortDto(
        String direction,
        String property,
        boolean ascending,
        boolean ignoreCase,
        String nullHandling
) {
    public static SortDto from(Sort.Order order) {
        return new SortDto(
                order.getDirection().name(),
                order.getProperty(),
                order.isAscending(),
                order.isIgnoreCase(),
                order.getNullHandling().name()
        );
    }
}
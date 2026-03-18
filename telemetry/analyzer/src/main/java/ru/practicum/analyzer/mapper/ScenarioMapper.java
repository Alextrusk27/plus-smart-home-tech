package ru.practicum.analyzer.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.analyzer.model.Scenario;

@Mapper(componentModel = "spring")
public interface ScenarioMapper {

    @Mapping(target = "id", ignore = true)
    Scenario toEntity(String hubId, String name);
}

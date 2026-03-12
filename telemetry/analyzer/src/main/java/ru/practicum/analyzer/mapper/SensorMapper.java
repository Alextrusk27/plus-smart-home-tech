package ru.practicum.analyzer.mapper;

import org.mapstruct.Mapper;
import ru.practicum.analyzer.model.Sensor;

@Mapper(componentModel = "spring")
public interface SensorMapper {

    Sensor toEntity(String hubId, String id);
}

package ru.practicum.analyzer.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.analyzer.model.Action;
import ru.yandex.practicum.kafka.telemetry.event.DeviceActionAvro;

@Mapper(componentModel = "spring")
public interface ActionMapper {

    @Mapping(target = "id", ignore = true)
    Action toEntity(DeviceActionAvro deviceActionAvro);
}

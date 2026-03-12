package ru.practicum.analyzer.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import ru.practicum.analyzer.model.Condition;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioConditionAvro;

@Mapper(componentModel = "spring")
public interface ConditionMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "type", source = "type")
    @Mapping(target = "operation", source = "operation")
    @Mapping(target = "value", source = "value", qualifiedByName = "valueToInteger")
    Condition toEntity(ScenarioConditionAvro scenarioConditionAvro);

    @Named("valueToInteger")
    default Integer valueToInteger(Object value) {
        return switch (value) {
            case null -> null;
            case Boolean b -> b ? 1 : 0;
            case Integer i -> i;
            default -> throw new IllegalArgumentException(
                    "Invalid value type for condition: " + value.getClass().getSimpleName() +
                            " with value: " + value
            );
        };
    }
}

package ru.practicum.interaction.api.constants;

import lombok.experimental.UtilityClass;

import java.math.BigDecimal;

@UtilityClass
public class BaseRateConstants {
    public static final BigDecimal VAT = BigDecimal.valueOf(0.1);
    public static final BigDecimal VAT_MULTIPLIER = BigDecimal.ONE.add(VAT);

    public static final BigDecimal BASE_DELIVERY_COST = BigDecimal.valueOf(5);

    public static final BigDecimal FRAGILE_MULTIPLIER = BigDecimal.valueOf(1.2);
    public static final BigDecimal DISTANCE_MULTIPLIER = BigDecimal.valueOf(1.2);

    public static final BigDecimal WEIGHT_RATE = BigDecimal.valueOf(0.3);
    public static final BigDecimal VOLUME_RATE = BigDecimal.valueOf(0.2);
}

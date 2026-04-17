package ru.practicum.interaction.api.constants;

import lombok.experimental.UtilityClass;

import java.math.BigDecimal;

@UtilityClass
public class VATConstants {
    public static final BigDecimal VAT = BigDecimal.valueOf(0.1);
    public static final BigDecimal VAT_MULTIPLIER = BigDecimal.ONE.add(VAT);
}

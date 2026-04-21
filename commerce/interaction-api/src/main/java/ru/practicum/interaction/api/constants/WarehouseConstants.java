package ru.practicum.interaction.api.constants;

import lombok.experimental.UtilityClass;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.util.Random;

@UtilityClass
public class WarehouseConstants {

    public static final String WAREHOUSE_1_ADDRESS = "ADDRESS_1";
    public static final String WAREHOUSE_2_ADDRESS = "ADDRESS_2";

    public static final BigDecimal WAREHOUSE_1_RATE = BigDecimal.ONE;
    public static final BigDecimal WAREHOUSE_2_RATE = BigDecimal.TWO;

    private static final String[] ADDRESSES =
            new String[]{WAREHOUSE_1_ADDRESS, WAREHOUSE_2_ADDRESS};

    public static final String CURRENT_ADDRESS =
            ADDRESSES[Random.from(new SecureRandom()).nextInt(0, ADDRESSES.length)];

}

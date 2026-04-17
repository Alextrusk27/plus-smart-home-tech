package ru.practicum.interaction.api.constants;

import lombok.experimental.UtilityClass;

import java.security.SecureRandom;
import java.util.Random;

@UtilityClass
public class WarehouseAddress {

    public static final String WAREHOUSE_1 = "ADDRESS_1";
    public static final String WAREHOUSE_2 = "ADDRESS_2";

    private static final String[] ADDRESSES =
            new String[]{WAREHOUSE_1, WAREHOUSE_2};

    public static final String CURRENT_ADDRESS =
            ADDRESSES[Random.from(new SecureRandom()).nextInt(0, ADDRESSES.length)];

}

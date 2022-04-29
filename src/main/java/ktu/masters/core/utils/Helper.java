package ktu.masters.core.utils;

import lombok.experimental.UtilityClass;

import java.util.function.Consumer;

@UtilityClass
public class Helper {
    private static final Consumer<Object> DO_NOTHING = __ -> {};
    private static final Consumer<Object> PRINT = System.out::println;

    public static final Consumer<Object> CONSUMER_FUNCTION = DO_NOTHING;
}

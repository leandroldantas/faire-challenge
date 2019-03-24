package util;

import java.util.Arrays;
import java.util.Objects;

public class ValidatorUtils {

    static final String EMPTY = "";

    public static boolean isValidValue(String... values){
        return Arrays.stream(values)
                .filter(it -> !Objects.isNull(values) && !EMPTY.equals(it)).count() == values.length;
    }

    public static boolean isValidValue(Object... values){
        return Arrays.stream(values).filter(Objects::isNull).count() == 0;
    }
}

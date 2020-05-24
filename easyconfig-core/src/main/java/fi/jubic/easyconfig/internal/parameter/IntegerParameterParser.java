package fi.jubic.easyconfig.internal.parameter;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class IntegerParameterParser extends PrimitiveParameterParser<Integer> {
    private static final Set<Class<?>> SUPPORTED_CLASSES = Stream
            .of(int.class, Integer.class)
            .collect(Collectors.toSet());

    @Override
    Set<Class<?>> supportedClasses() {
        return SUPPORTED_CLASSES;
    }

    @Override
    Optional<Integer> parse(String stringValue) {
        try {
            return Optional.of(Integer.valueOf(stringValue));
        }
        catch (NumberFormatException ignore) {
            return Optional.empty();
        }
    }
}

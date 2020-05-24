package fi.jubic.easyconfig.internal.parameter;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class FloatParameterParser extends PrimitiveParameterParser<Float> {
    private static final Set<Class<?>> SUPPORTED_CLASSES = Stream
            .of(float.class, Float.class)
            .collect(Collectors.toSet());

    @Override
    Set<Class<?>> supportedClasses() {
        return SUPPORTED_CLASSES;
    }

    @Override
    Optional<Float> parse(String stringValue) {
        try {
            return Optional.of(Float.valueOf(stringValue));
        }
        catch (NumberFormatException ignore) {
            return Optional.empty();
        }
    }
}

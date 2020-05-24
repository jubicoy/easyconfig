package fi.jubic.easyconfig.internal.parameter;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class DoubleParameterParser extends PrimitiveParameterParser<Double> {
    private static final Set<Class<?>> SUPPORTED_CLASSES = Stream
            .of(double.class, Double.class)
            .collect(Collectors.toSet());

    @Override
    Set<Class<?>> supportedClasses() {
        return SUPPORTED_CLASSES;
    }

    @Override
    Optional<Double> parse(String stringValue) {
        try {
            return Optional.of(Double.valueOf(stringValue));
        }
        catch (NumberFormatException ignore) {
            return Optional.empty();
        }
    }
}

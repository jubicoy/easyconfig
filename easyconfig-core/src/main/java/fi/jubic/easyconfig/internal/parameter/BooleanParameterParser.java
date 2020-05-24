package fi.jubic.easyconfig.internal.parameter;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class BooleanParameterParser extends PrimitiveParameterParser<Boolean> {
    private static final Set<Class<?>> SUPPORTED_CLASSES = Stream
            .of(boolean.class, Boolean.class)
            .collect(Collectors.toSet());

    @Override
    Set<Class<?>> supportedClasses() {
        return SUPPORTED_CLASSES;
    }

    @Override
    Optional<Boolean> parse(String stringValue) {
        String normalized = stringValue.trim().toLowerCase();
        if (!normalized.equals("true") && !normalized.equals("false")) {
            return Optional.empty();
        }
        return Optional.of(Boolean.valueOf(stringValue));
    }
}

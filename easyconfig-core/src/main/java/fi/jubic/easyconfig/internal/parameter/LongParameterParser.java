package fi.jubic.easyconfig.internal.parameter;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class LongParameterParser extends PrimitiveParameterParser<Long> {
    private static final Set<Class<?>> SUPPORTED_CLASSES = Stream
            .of(long.class, Long.class)
            .collect(Collectors.toSet());

    @Override
    Set<Class<?>> supportedClasses() {
        return SUPPORTED_CLASSES;
    }

    @Override
    Optional<Long> parse(String stringValue) {
        try {
            return Optional.of(Long.valueOf(stringValue));
        }
        catch (NumberFormatException ignore) {
            return Optional.empty();
        }
    }
}

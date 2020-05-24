package fi.jubic.easyconfig.internal.parameter;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class StringParameterParser extends PrimitiveParameterParser<String> {
    private static final Set<Class<?>> SUPPORTED_CLASSES = Stream
            .of(String.class)
            .collect(Collectors.toSet());

    @Override
    Set<Class<?>> supportedClasses() {
        return SUPPORTED_CLASSES;
    }

    @Override
    Optional<String> parse(String stringValue) {
        return Optional.of(stringValue);
    }
}

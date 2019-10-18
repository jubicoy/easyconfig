package fi.jubic.easyconfig;

import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MappingException extends Exception {
    public MappingException() {
        super();
    }

    public MappingException(String message) {
        super(message);
    }

    public MappingException(String message, Throwable cause) {
        super(message, cause);
    }

    public MappingException(Throwable cause) {
        super(cause);
    }

    public MappingException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    static MappingException from(InternalMappingException exception) {
        return new MappingException(
                flattenNestedExceptions(exception)
                        .map(InternalMappingException::getMessage)
                        .collect(Collectors.joining("\n"))
                        + "\n",
                exception
        );
    }

    private static Stream<InternalMappingException> flattenNestedExceptions(InternalMappingException exception) {
        if (exception.getNestedExceptions().isEmpty()) {
            return Stream.of(exception);
        }
        return exception.getNestedExceptions()
                .stream()
                .flatMap(
                        MappingException::flattenNestedExceptions
                );
    }
}

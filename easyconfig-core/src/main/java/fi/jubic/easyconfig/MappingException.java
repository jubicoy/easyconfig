package fi.jubic.easyconfig;

/**
 * Exception describing mapping errors.
 *
 * @deprecated Replaced by {@link IllegalArgumentException}
 */
@Deprecated
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

    public MappingException(
            String message,
            Throwable cause,
            boolean enableSuppression,
            boolean writableStackTrace
    ) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}

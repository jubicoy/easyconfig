package fi.jubic.easyconfig;

import java.util.Collections;
import java.util.List;

class InternalMappingException extends Exception {
    private final List<InternalMappingException> nestedExceptions;

    public InternalMappingException() {
        super();
        this.nestedExceptions = Collections.emptyList();
    }

    public InternalMappingException(List<InternalMappingException> nestedExceptions) {
        super();
        this.nestedExceptions = nestedExceptions;
    }

    public InternalMappingException(String message) {
        super(message);
        this.nestedExceptions = Collections.emptyList();
    }

    public InternalMappingException(String message, List<InternalMappingException> nestedExceptions) {
        super(message);
        this.nestedExceptions = nestedExceptions;
    }

    public InternalMappingException(String message, Throwable cause) {
        super(message, cause);
        this.nestedExceptions = Collections.emptyList();
    }

    public InternalMappingException(String message, Throwable cause, List<InternalMappingException> nestedExceptions) {
        super(message, cause);
        this.nestedExceptions = nestedExceptions;
    }

    public InternalMappingException(Throwable cause) {
        super(cause);
        this.nestedExceptions = Collections.emptyList();
    }

    public InternalMappingException(Throwable cause, List<InternalMappingException> nestedExceptions) {
        super(cause);
        this.nestedExceptions = nestedExceptions;
    }

    protected InternalMappingException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.nestedExceptions = Collections.emptyList();
    }

    protected InternalMappingException(String message, Throwable cause, List<InternalMappingException> nestedExceptions, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.nestedExceptions = nestedExceptions;
    }

    public List<InternalMappingException> getNestedExceptions() {
        return nestedExceptions;
    }
}

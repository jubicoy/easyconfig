package fi.jubic.easyconfig;

public class MappingException extends Exception {
    MappingException(String message) {
        super(message);
    }
    MappingException(Throwable throwable) {
        super(throwable);
    }
}

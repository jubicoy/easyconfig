package fi.jubic.easyconfig;

public class MappingException extends Exception {
    MappingException(String message) {
        super(message);
    }
    public MappingException(Throwable throwable) {
        super(throwable);
    }
}

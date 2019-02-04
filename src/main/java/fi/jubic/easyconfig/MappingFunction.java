package fi.jubic.easyconfig;

public interface MappingFunction<T, R> {
    R apply(T var1) throws MappingException;
}

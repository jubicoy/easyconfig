package fi.jubic.easyconfig;

interface MappingFunction<T, R> {
    R apply(T var1) throws MappingException;
}

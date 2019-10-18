package fi.jubic.easyconfig;

interface Initializer<T> {
    T initialize(EnvProvider prefixedProvider) throws InternalMappingException;
}

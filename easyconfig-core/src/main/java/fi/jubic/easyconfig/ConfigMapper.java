package fi.jubic.easyconfig;

public class ConfigMapper {
    private final EnvProvider envProvider;

    /**
     * Default constructor that automatically initializes with default provider.
     */
    public ConfigMapper() {
        this.envProvider = EnvProvider.getDefault();
    }

    /**
     * Provide custom env provider.
     * @param envProvider {@link EnvProvider} for resolving variables
     */
    public ConfigMapper(EnvProvider envProvider) {
        this.envProvider = envProvider;
    }

    /**
     * Read configuration from environment and map it to an Object.
     * @param klass Class for initializing the configuration object
     */
    public <T> T read(Class<T> klass) throws MappingException {
        return read("", klass);
    }

    /**
     * Read configuration from environment with prefixed names and map it to an Object.
     * @param prefix The prefix to strip, e.g. "APP_"
     * @param klass Class for initializing the configuration object
     */
    public <T> T read(String prefix, Class<T> klass) throws MappingException {
        return new InitializerBuilder(this)
                .build(klass)
                .initialize(name -> envProvider.getVariable(prefix + name));
    }
}

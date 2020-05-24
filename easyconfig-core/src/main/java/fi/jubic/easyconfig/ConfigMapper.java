package fi.jubic.easyconfig;

import fi.jubic.easyconfig.internal.ConfigPropertyDef;
import fi.jubic.easyconfig.internal.MappingContext;
import fi.jubic.easyconfig.internal.Result;
import fi.jubic.easyconfig.internal.initializers.Initializer;
import fi.jubic.easyconfig.internal.initializers.RootInitializerParser;
import fi.jubic.easyconfig.providers.EnvProvider;

import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
     * @param configClass Class for initializing the configuration object
     */
    public <T> T read(Class<T> configClass) {
        return read("", configClass);
    }

    /**
     * Read configuration from environment with prefixed names and map it to an Object.
     * @param prefix The prefix to strip, e.g. "APP_"
     * @param configClass Class for initializing the configuration object
     */
    public <T> T read(String prefix, Class<T> configClass) {
        ConfigPropertyDef propertyDef = new ConfigPropertyDef(
                prefix,
                configClass
        );

        Result<Initializer<T>> initializerResult = new RootInitializerParser()
                .<T>parse(new MappingContext(), propertyDef)
                .orElseThrow(() -> new IllegalArgumentException(
                        String.format(
                                "No suitable initializer found for %s",
                                configClass.getCanonicalName()
                        )
                ));
        if (initializerResult.hasMessages()) {
            throw new IllegalArgumentException(
                    Stream
                            .of(
                                    Stream.of("Invalid configuration definition:"),
                                    initializerResult.getMessagesAsStringStream()
                                            .map(row -> "    " + row)
                            )
                            .flatMap(Function.identity())
                            .collect(Collectors.joining("\n"))
            );
        }

        Result<T> result = initializerResult.flatMap(
                initializer -> initializer.initialize(prefix, envProvider)
        );

        if (result.hasMessages()) {
            throw new IllegalArgumentException(
                    Stream
                            .of(
                                    Stream.of("Invalid configuration parameter(s):"),
                                    result.getMessagesAsStringStream()
                                            .map(row -> "    " + row)
                            )
                            .flatMap(Function.identity())
                            .collect(Collectors.joining("\n"))
            );
        }

        return result.getValue();
    }
}

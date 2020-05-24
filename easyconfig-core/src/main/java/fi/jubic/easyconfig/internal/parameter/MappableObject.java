package fi.jubic.easyconfig.internal.parameter;

import fi.jubic.easyconfig.internal.Result;
import fi.jubic.easyconfig.internal.initializers.Initializer;
import fi.jubic.easyconfig.providers.EnvProvider;

public class MappableObject<T> implements Mappable<T> {
    private final String prefix;
    private final Initializer<T> initializer;

    public MappableObject(
            String prefix,
            Initializer<T> initializer
    ) {
        this.prefix = prefix;
        this.initializer = initializer;
    }

    @Override
    public Result<T> initialize(String prefix, EnvProvider envProvider) {
        return initializer.initialize(prefix + this.prefix, envProvider);
    }
}

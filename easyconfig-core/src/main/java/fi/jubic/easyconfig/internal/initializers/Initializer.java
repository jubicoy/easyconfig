package fi.jubic.easyconfig.internal.initializers;

import fi.jubic.easyconfig.internal.Result;
import fi.jubic.easyconfig.providers.EnvProvider;

public interface Initializer<T> {
    Result<T> initialize(
            String prefix,
            EnvProvider envProvider
    );
}

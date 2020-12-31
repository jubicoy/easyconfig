package fi.jubic.easyconfig.internal.parameter;

import fi.jubic.easyconfig.internal.Result;
import fi.jubic.easyconfig.providers.EnvProvider;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.function.BiFunction;

/**
 * A single parameter instance that can be injected into an initializer.
 *
 * @param <T> the type of the parameter
 */
public class MappableParameter<T> implements Mappable<T> {
    private final String variableName;
    private final Class<?> parameterClass;
    private final boolean nullable;
    private final boolean noPrefix;
    private final BiFunction<String, String, Result<T>> parser;
    @Nullable
    private final T defaultValue;

    MappableParameter(
            String variableName,
            Class<?> parameterClass,
            boolean nullable,
            boolean noPrefix,
            BiFunction<String, String, Result<T>> parser,
            @Nullable T defaultValue
    ) {
        if (nullable && (defaultValue != null)) {
            throw new IllegalStateException();
        }
        this.variableName = variableName;
        this.parameterClass = parameterClass;
        this.nullable = nullable;
        this.noPrefix = noPrefix;
        this.parser = parser;
        this.defaultValue = defaultValue;
    }

    @Override
    public Result<T> initialize(String prefix, EnvProvider envProvider) {
        String effectiveVariableName = noPrefix
                ? variableName
                : prefix + variableName;
        return envProvider.getVariable(effectiveVariableName)
                .map(strVal -> parser.apply(prefix, strVal))
                .orElseGet(() -> {
                    if (nullable) {
                        return Result.of(null);
                    }
                    return Optional.ofNullable(defaultValue)
                            .map(Result::of)
                            .orElseGet(() -> Result.message(
                                    String.format(
                                            "Missing variable \"%s\" [%s]",
                                            effectiveVariableName,
                                            parameterClass.getSimpleName()
                                    )
                            ));
                });
    }
}

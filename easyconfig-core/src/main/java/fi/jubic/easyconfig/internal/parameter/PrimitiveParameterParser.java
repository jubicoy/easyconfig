package fi.jubic.easyconfig.internal.parameter;

import fi.jubic.easyconfig.internal.ConfigPropertyDef;
import fi.jubic.easyconfig.internal.MappingContext;
import fi.jubic.easyconfig.internal.Result;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class PrimitiveParameterParser<T> implements ParameterParser {
    private static final Set<Class<?>> TRUE_PRIMITIVES = Stream
            .of(
                    boolean.class,
                    short.class,
                    byte.class,
                    int.class,
                    long.class,
                    float.class,
                    double.class,
                    char.class
            )
            .collect(Collectors.toSet());

    abstract Set<Class<?>> supportedClasses();

    abstract Optional<T> parse(String stringValue);

    @Override
    public Optional<Result<Mappable<?>>> parseParameter(
            MappingContext context,
            ConfigPropertyDef propertyDef
    ) {
        if (!supportedClasses().contains(propertyDef.getPropertyClass())) {
            return Optional.empty();
        }

        MappingContext currentContext = context.push(
                String.format(
                        "\"%s\" [%s]",
                        propertyDef.getVariableName(),
                        propertyDef.getPropertyClass().getSimpleName()
                )
        );

        if (propertyDef.isNullable() && TRUE_PRIMITIVES.contains(propertyDef.getPropertyClass())) {
            return Optional.of(
                    Result.message(
                            currentContext.format("Primitive type cannot be nullable")
                    )
            );
        }

        Result<Mappable<?>> mappableResult = propertyDef.getDefaultValue()
                .map(defaultValue -> parse(defaultValue)
                        .map(Result::of)
                        .orElseGet(
                                () -> Result.message(
                                        currentContext.format(
                                                String.format(
                                                        "Invalid defaultValue \"%s\"",
                                                        defaultValue
                                                )
                                        )
                                )
                        )
                )
                .orElseGet(() -> Result.of(null))
                .map(defaultValue -> new MappableParameter<>(
                        propertyDef.getVariableName(),
                        propertyDef.getPropertyClass(),
                        propertyDef.isNullable(),
                        propertyDef.isNoPrefix(),
                        (prefix, str) -> parse(str)
                                .map(Result::of)
                                .orElseGet(() -> Result.message(
                                        String.format(
                                                "\"%s%s\" [%s]: Could not parse \"%s\"",
                                                propertyDef.isNoPrefix() ? "" : prefix,
                                                propertyDef.getVariableName(),
                                                propertyDef.getPropertyClass().getSimpleName(),
                                                str
                                        )
                                )),
                        defaultValue
                ));

        return Optional.of(mappableResult);
    }
}

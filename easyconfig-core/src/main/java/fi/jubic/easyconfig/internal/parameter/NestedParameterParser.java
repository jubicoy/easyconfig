package fi.jubic.easyconfig.internal.parameter;

import fi.jubic.easyconfig.internal.ConfigPropertyDef;
import fi.jubic.easyconfig.internal.MappingContext;
import fi.jubic.easyconfig.internal.Result;
import fi.jubic.easyconfig.internal.initializers.Initializer;
import fi.jubic.easyconfig.internal.initializers.InitializerParser;
import fi.jubic.easyconfig.internal.initializers.RootInitializerParser;

import java.util.Optional;

public class NestedParameterParser implements ParameterParser {
    private static final InitializerParser INITIALIZER_PARSER = new RootInitializerParser();

    @Override
    public Optional<Result<Mappable<?>>> parseParameter(
            MappingContext context,
            ConfigPropertyDef propertyDef
    ) {
        MappingContext currentContext = context.push(
                String.format(
                        "\"%s\" [%s]",
                        propertyDef.getVariableName(),
                        propertyDef.getPropertyClass()
                )
        );

        if (propertyDef.getDefaultValue().isPresent()) {
            return Optional.of(
                    Result.message(
                            currentContext.format(
                                    "defaultValue is not allowed for a nested config object"
                            )
                    )
            );
        }

        if (propertyDef.isNullable()) {
            return Optional.of(
                    Result.message(
                            currentContext.format(
                                    "nullable is not allowed for a nested config object"
                            )
                    )
            );
        }

        return INITIALIZER_PARSER.parse(currentContext, propertyDef)
                .map(result -> result.flatMap(
                        initializer -> buildWithInitializer(
                                propertyDef,
                                initializer
                        )
                ));
    }

    private Result<Mappable<?>> buildWithInitializer(
            ConfigPropertyDef propertyDef,
            Initializer<?> initializer
    ) {
        Mappable<?> mappableParameter = new MappableObject<>(
                propertyDef.getVariableName(),
                initializer
        );

        return Result.of(mappableParameter);
    }
}

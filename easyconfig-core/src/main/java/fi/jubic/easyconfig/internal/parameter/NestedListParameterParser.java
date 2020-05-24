package fi.jubic.easyconfig.internal.parameter;

import fi.jubic.easyconfig.internal.ConfigPropertyDef;
import fi.jubic.easyconfig.internal.MappingContext;
import fi.jubic.easyconfig.internal.Result;
import fi.jubic.easyconfig.internal.initializers.Initializer;
import fi.jubic.easyconfig.internal.initializers.InitializerParser;
import fi.jubic.easyconfig.internal.initializers.RootInitializerParser;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class NestedListParameterParser implements ParameterParser {
    private static final InitializerParser INITIALIZER_PARSER = new RootInitializerParser();

    @Override
    public Optional<Result<Mappable<?>>> parseParameter(
            MappingContext context,
            ConfigPropertyDef propertyDef
    ) {
        if (!List.class.isAssignableFrom(propertyDef.getPropertyClass())) {
            return Optional.empty();
        }

        MappingContext currentContext = context.push(
                String.format(
                        "\"%s\" [List<%s>]",
                        propertyDef.getVariableName(),
                        propertyDef.getTypeArguments().get(0)
                )
        );

        if (propertyDef.getTypeArguments().isEmpty()) {
            return Optional.empty();
        }

        if (!propertyDef.getVariableName().contains("{}")) {
            return Optional.of(
                    Result.message(
                            currentContext.format("Missing index placeholder \"{}\"")
                    )
            );
        }

        if (propertyDef.isNullable()) {
            return Optional.of(
                    Result.message(
                            currentContext.format("nullable is not allowed for a nested list")
                    )
            );
        }

        Result<Initializer<Object>> elementInitializerResult = INITIALIZER_PARSER
                .parse(
                        currentContext,
                        new ConfigPropertyDef(
                                "",
                                propertyDef.getTypeArguments().get(0)
                        )
                )
                .orElseGet(() -> Result.message(
                        currentContext.format(
                                String.format(
                                        "No suitable initializer found for %s",
                                        propertyDef.getTypeArguments().get(0).getCanonicalName()
                                )
                        )
                ));

        if (elementInitializerResult.hasMessages()) {
            return Optional.of(
                    Result.message(elementInitializerResult.getMessages())
            );
        }

        Result<Mappable<?>> mappableResult = Result.of(
                (prefix, envProvider) -> Result.unwrap(
                        envProvider
                                .getKeysMatching(prefix + propertyDef.getVariableName())
                                .map(listKey -> new MappableObject<>(
                                        propertyDef.getVariableName().replace("{}", listKey),
                                        elementInitializerResult.getValue()
                                ))
                                .map(mappableObject -> mappableObject.initialize(
                                        prefix,
                                        envProvider
                                ))
                                .collect(Collectors.toList())
                        )
                        .map(list -> (Object) list)
        );

        return Optional.of(mappableResult);
    }
}

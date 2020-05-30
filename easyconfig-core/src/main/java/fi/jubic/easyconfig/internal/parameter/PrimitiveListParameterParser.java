package fi.jubic.easyconfig.internal.parameter;

import fi.jubic.easyconfig.internal.ConfigPropertyDef;
import fi.jubic.easyconfig.internal.MappingContext;
import fi.jubic.easyconfig.internal.Result;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PrimitiveListParameterParser implements ParameterParser {
    static final List<PrimitiveParameterParser<?>> PRIMITIVE_PARSERS = Arrays.asList(
            new BooleanParameterParser(),
            new IntegerParameterParser(),
            new LongParameterParser(),
            new FloatParameterParser(),
            new DoubleParameterParser(),
            new StringParameterParser()
    );

    @Override
    public Optional<Result<Mappable<?>>> parseParameter(
            MappingContext context,
            ConfigPropertyDef propertyDef
    ) {
        if (!List.class.equals(propertyDef.getPropertyClass())) {
            return Optional.empty();
        }

        MappingContext currentContext = context.push(
                String.format(
                        "\"%s\" [List<%s>]",
                        propertyDef.getVariableName(),
                        propertyDef.getTypeArguments().get(0).getSimpleName()
                )
        );

        // Strictly according to the ParameterParser semantics this is not an error case. It is
        // a valid property definition without a matching parser. However, for convenience it is
        // treated as an error here to provide a more descriptive error message without registering
        // a parser responsible only for broadcasting this message.
        if (propertyDef.getTypeArguments().isEmpty()) {
            return Optional.of(
                    Result.message(
                            currentContext.format("Cannot map a List without a type argument")
                    )
            );
        }

        if (propertyDef.isNullable()) {
            return Optional.of(
                    Result.message(
                            currentContext.format("nullable can't be applied to primitive lists")
                    )
            );
        }

        Optional<PrimitiveParameterParser<?>> optionalPrimitiveParser = PRIMITIVE_PARSERS.stream()
                .filter(parser -> parser.supportedClasses()
                        .contains(propertyDef.getTypeArguments().get(0))
                )
                .findFirst();
        if (!optionalPrimitiveParser.isPresent()) {
            return Optional.empty();
        }
        PrimitiveParameterParser<?> primitiveParser = optionalPrimitiveParser.get();

        Result<List<?>> defaultValueResult = propertyDef.getDefaultValue()
                .map(defaultValue -> parse(
                        currentContext.subContext(),
                        primitiveParser,
                        defaultValue,
                        propertyDef.getListDelimiter()
                ))
                .orElseGet(() -> Result.of(null));
        if (defaultValueResult.hasMessages()) {
            return Optional.of(
                    Result.unwrapMessages(
                            Result.message(
                                    currentContext.format("Invalid default value")
                            ),
                            defaultValueResult
                    )
            );
        }

        Result<Mappable<?>> mappableResult = defaultValueResult
                .map(defaultValue -> new MappableParameter<>(
                        propertyDef.getVariableName(),
                        propertyDef.getPropertyClass(),
                        propertyDef.isNullable(),
                        propertyDef.isNoPrefix(),
                        (prefix, str) -> parse(
                                new MappingContext(
                                        String.format(
                                                "\"%s%s\" [List<%s>]",
                                                propertyDef.isNoPrefix() ? "" : prefix,
                                                propertyDef.getVariableName(),
                                                propertyDef.getTypeArguments()
                                                        .get(0)
                                                        .getSimpleName()
                                        )
                                ),
                                primitiveParser,
                                str,
                                propertyDef.getListDelimiter()
                        ),
                        defaultValue
                ));

        return Optional.of(mappableResult);
    }

    private Result<List<?>> parse(
            MappingContext context,
            PrimitiveParameterParser<?> primitiveParser,
            String stringValue,
            String delimiter
    ) {
        if (stringValue.isEmpty()) {
            return Result.of(Collections.emptyList());
        }

        MappingContext elementContext = context.subContext();

        Result<List<?>> elementsResult = Stream.of(stringValue.split(delimiter))
                .map(element -> primitiveParser.parse(element)
                        .map(Result::of)
                        .orElseGet(() -> Result.message(
                                elementContext.format(
                                        String.format(
                                                "Could not parse \"%s\" in \"%s\"",
                                                element,
                                                stringValue
                                        )
                                )
                        ))
                )
                .collect(
                        Collectors.collectingAndThen(
                                Collectors.<Result<?>>toList(),
                                Result::unsafeUnwrap
                        )
                );

        if (elementsResult.hasMessages()) {
            return Result.unwrapMessages(
                    Result.message(context.format("Invalid value(s)")),
                    elementsResult
            );
        }

        return elementsResult;
    }
}

package fi.jubic.easyconfig.internal.parameter;

import fi.jubic.easyconfig.internal.ConfigPropertyDef;
import fi.jubic.easyconfig.internal.MappingContext;
import fi.jubic.easyconfig.internal.Result;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RootConfigParameterParser implements ParameterParser {
    private static final List<ParameterParser> NESTED_PARSERS = Stream
            .of(
                    PrimitiveListParameterParser.PRIMITIVE_PARSERS.stream(),
                    Stream.of(
                            new EnvProviderParameterParser(),
                            new PrimitiveListParameterParser(),
                            new NestedListParameterParser(),
                            new NestedParameterParser()
                    )
            )
            .<ParameterParser>flatMap(Function.identity())
            .collect(Collectors.toList());

    @Override
    public Optional<Result<Mappable<?>>> parseParameter(
            MappingContext context,
            ConfigPropertyDef propertyDef
    ) {
        return NESTED_PARSERS.stream()
                .map(parser -> parser.parseParameter(context, propertyDef))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .findFirst();
    }
}

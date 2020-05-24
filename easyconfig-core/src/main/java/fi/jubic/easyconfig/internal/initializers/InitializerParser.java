package fi.jubic.easyconfig.internal.initializers;

import fi.jubic.easyconfig.annotations.ConfigProperty;
import fi.jubic.easyconfig.annotations.EasyConfigProperty;
import fi.jubic.easyconfig.annotations.EnvProviderProperty;
import fi.jubic.easyconfig.internal.ConfigPropertyDef;
import fi.jubic.easyconfig.internal.MappingContext;
import fi.jubic.easyconfig.internal.Result;
import fi.jubic.easyconfig.internal.parameter.ParameterParser;
import fi.jubic.easyconfig.internal.parameter.RootConfigParameterParser;

import java.lang.annotation.Annotation;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public interface InitializerParser {
    ParameterParser PARAMETER_PARSER = new RootConfigParameterParser();

    @SuppressWarnings("deprecation")
    Set<Class<? extends Annotation>> SUPPORTED_PROPERTY_ANNOTATIONS = Stream
            .of(ConfigProperty.class, EasyConfigProperty.class, EnvProviderProperty.class)
            .collect(Collectors.toSet());

    <T> Optional<Result<Initializer<T>>> parse(
            MappingContext context,
            ConfigPropertyDef propertyDef
    );
}

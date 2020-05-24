package fi.jubic.easyconfig.internal.initializers;

import fi.jubic.easyconfig.extensions.ConfigExtension;
import fi.jubic.easyconfig.extensions.ConfigExtensionProvider;
import fi.jubic.easyconfig.internal.ConfigPropertyDef;
import fi.jubic.easyconfig.internal.MappingContext;
import fi.jubic.easyconfig.internal.Result;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RootInitializerParser implements InitializerParser {
    private static final List<InitializerParser> PARSERS = Arrays.asList(
            new ParameterizedConstructorInitializerParser(),
            new SetterInitializerParser(),
            new BuilderClassInitializerParser()
    );

    @Override
    public <T> Optional<Result<Initializer<T>>> parse(
            MappingContext context,
            ConfigPropertyDef propertyDef
    ) {
        return PARSERS.stream()
                .map(parser -> parser.<T>parse(context, propertyDef))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .findFirst()
                .map(result -> result.flatMap(
                        initializer -> wrapExtensions(propertyDef, initializer)
                ));
    }

    private <T> Result<Initializer<T>> wrapExtensions(
            ConfigPropertyDef propertyDef,
            Initializer<T> initializer
    ) {
        Optional<AnnotatedElement> optionalElement = propertyDef.getAnnotatedElement();
        if (!optionalElement.isPresent()) {
            return Result.of(initializer);
        }

        List<? extends Annotation> extensionAnnotations = Stream
                .of(optionalElement.get().getAnnotations())
                .filter(annotation -> Objects.nonNull(
                        annotation.annotationType()
                                .getAnnotation(ConfigExtension.class)
                ))
                .collect(Collectors.toList());

        if (extensionAnnotations.isEmpty()) {
            return Result.of(initializer);
        }

        Initializer<T> wrappedInitializer = initializer;

        for (Annotation annotation : extensionAnnotations) {
            ConfigPropertyDef providerDef = ConfigPropertyDef.buildForExtensionProvider(
                    annotation
            );

            MappingContext context = new MappingContext(propertyDef.getPropertyClass());

            final Initializer<T> finalWrappedInitializer = wrappedInitializer;

            Result<Initializer<T>> wrappedInitializerResult = PARSERS.stream()
                    .map(parser -> parser.<ConfigExtensionProvider<Annotation, T>>parse(
                            context,
                            providerDef
                    ))
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .findFirst()
                    .orElseGet(() -> Result.message(
                            context.format("No suitable initializer found")
                    ))
                    .map(providerInitializer -> (prefix, envProvider) -> finalWrappedInitializer
                            .initialize(prefix, envProvider)
                            .flatMap(wrappedInstance -> providerInitializer
                                    .initialize(prefix, envProvider)
                                    .map(provider -> provider.extend(annotation, wrappedInstance))
                            )
                    );

            if (wrappedInitializerResult.hasMessages()) {
                return wrappedInitializerResult;
            }
            wrappedInitializer = wrappedInitializerResult.getValue();
        }
        return Result.of(wrappedInitializer);
    }
}

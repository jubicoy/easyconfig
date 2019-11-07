package fi.jubic.easyconfig;

import fi.jubic.easyconfig.annotations.EasyConfig;
import fi.jubic.easyconfig.annotations.EasyConfigProperty;

import java.lang.reflect.Method;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class InitializerBuilder {
    private final ConfigMapper mapper;

    InitializerBuilder(ConfigMapper mapper) {
        this.mapper = mapper;
    }

    <T> Initializer<T> build(Class<T> klass) throws InternalMappingException {
        Initializer<T> initializer = getParameterizedConstructor(klass)
                .orElseGet(
                        () -> getBuilderConstructor(klass)
                                .orElseGet(
                                        () -> getDefaultConstructor(klass)
                                                .orElse(null)
                                )
                );

        if (initializer == null) {
            throw new InternalMappingException("No mappings found for " + klass.getCanonicalName());
        }

        return initializer;
    }

    private <T> Optional<Initializer<T>> getDefaultConstructor(Class<T> klass) {
        ParameterParser parameterParser = new ParameterParser(mapper, this);

        //noinspection unchecked
        return Stream.of(klass.getConstructors())
                .filter(constructor -> constructor.getParameterCount() == 0)
                .findFirst()
                .map(constructor -> new DefaultConstructorInitializer<>(
                        (java.lang.reflect.Constructor<T>)constructor,
                        Stream.of(klass.getMethods())
                                .filter(
                                        method -> method.getAnnotation(
                                                EasyConfigProperty.class
                                        ) != null
                                )
                                .filter(method -> method.getReturnType() != null)
                                .filter(method -> method.getParameterCount() == 1)
                                .map(method -> parameterParser.parseParameter(
                                        method.getParameters()[0],
                                        method
                                ))
                                .filter(Optional::isPresent)
                                .map(Optional::get)
                                .collect(Collectors.toList())
                ));
    }

    private <T> Optional<Initializer<T>> getParameterizedConstructor(Class<T> klass) {
        return Stream.of(klass.getConstructors())
                .filter(construct -> construct.getParameterCount() > 0)
                .filter(construct -> Stream.of(construct.getParameters())
                        .allMatch(
                                parameter -> parameter.getAnnotation(
                                        EasyConfigProperty.class
                                ) != null
                        )
                )
                .max(Comparator.comparing(java.lang.reflect.Constructor::getParameterCount))
                .flatMap(constructor -> {
                    List<Optional<MappableParameter>> parameters = Stream
                            .of(constructor.getParameters())
                            .map(parameter -> new ParameterParser(mapper, this).parseParameter(
                                    parameter,
                                    null
                            ))
                            .collect(Collectors.toList());

                    if (parameters.stream().anyMatch(parameter -> !parameter.isPresent())) {
                        return Optional.empty();
                    }

                    //noinspection unchecked
                    return Optional.of(
                            new ParameterizedConstructorInitializer<>(
                                    (java.lang.reflect.Constructor<T>) constructor,
                                    parameters.stream()
                                            .filter(Optional::isPresent)
                                            .map(Optional::get)
                                            .collect(Collectors.toList())
                            )
                    );
                });
    }

    private <T> Optional<Initializer<T>> getBuilderConstructor(Class<T> klass) {
        EasyConfig easyConfig = klass.getAnnotation(EasyConfig.class);
        if (easyConfig == null) return Optional.empty();
        if (easyConfig.builder().equals(Void.class)) return Optional.empty();

        Optional<Method> buildMethod = Stream.of(easyConfig.builder().getMethods())
                .filter(method -> method.getParameterCount() == 0)
                .filter(method -> method.getReturnType().equals(klass))
                .findFirst();
        if (!buildMethod.isPresent()) {
            new InternalMappingException(
                    "No build method found in "
                            + easyConfig.builder().getCanonicalName()
                            + ". This exception is not thrown. It's no use trying to catch it."
                            + " I'm sorry."
            ).printStackTrace();
            return Optional.empty();
        }

        ParameterParser parameterParser = new ParameterParser(mapper, this);
        return Optional.of(
                new ClassBuilderInitializer<>(
                        easyConfig.builder(),
                        buildMethod.get(),
                        Stream.of(easyConfig.builder().getMethods())
                                .filter(
                                        method -> method.getAnnotation(
                                                EasyConfigProperty.class
                                        ) != null
                                )
                                .filter(method -> method.getReturnType() == easyConfig.builder())
                                .filter(method -> method.getParameterCount() == 1)
                                .map(method -> parameterParser.parseParameter(
                                        method.getParameters()[0],
                                        method
                                ))
                                .filter(Optional::isPresent)
                                .map(Optional::get)
                                .collect(Collectors.toList())
                )
        );
    }
}

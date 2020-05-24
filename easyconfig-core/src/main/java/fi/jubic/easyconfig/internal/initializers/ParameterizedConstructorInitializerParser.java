package fi.jubic.easyconfig.internal.initializers;

import fi.jubic.easyconfig.internal.ConfigPropertyDef;
import fi.jubic.easyconfig.internal.MappingContext;
import fi.jubic.easyconfig.internal.Result;
import fi.jubic.easyconfig.internal.parameter.Mappable;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class ParameterizedConstructorInitializerParser implements InitializerParser {
    @Override
    public <T> Optional<Result<Initializer<T>>> parse(
            MappingContext context,
            ConfigPropertyDef propertyDef
    ) {
        Class<?> propertyClass = propertyDef.getPropertyClass();

        //noinspection unchecked
        Constructor<T>[] allConstructors = (Constructor<T>[])propertyDef
                .getPropertyClass()
                .getConstructors();

        List<Constructor<T>> constructors = Stream.of(allConstructors)
                .filter(constructor -> constructor.getParameterCount() > 0)
                .filter(construct -> Stream.of(construct.getParameters())
                        .allMatch(
                                parameter -> SUPPORTED_PROPERTY_ANNOTATIONS.stream()
                                        .map(parameter::getAnnotation)
                                        .anyMatch(Objects::nonNull)
                        )
                )
                .collect(Collectors.toList());

        if (constructors.isEmpty()) {
            return Optional.empty();
        }

        MappingContext currentContext = context.push(
                String.format(
                        "%s (constructor initializer)",
                        propertyClass.getCanonicalName()
                )
        );

        if (constructors.size() > 1) {
            MappingContext subContext = currentContext.subContext();
            return Stream
                    .of(
                            Stream.of(
                                    Result.<Initializer<T>>message(
                                            currentContext.format(
                                                    "Multiple suitable constructors found"
                                            )
                                    )
                            ),
                            constructors.stream()
                                    .map(Constructor::toString)
                                    .map(subContext::format)
                                    .map(Result::<Initializer<T>>message)
                    )
                    .flatMap(Function.identity())
                    .collect(
                            Collectors.collectingAndThen(
                                    Collectors.<Result<?>>toList(),
                                    list -> Optional.of(Result.unwrapMessages(list))
                            )
                    );
        }

        Constructor<T> constructor = constructors.get(0);

        Result<List<Mappable<?>>> parametersResult = Stream.of(constructor.getParameters())
                .map(parameter -> PARAMETER_PARSER
                                .parseParameter(
                                        currentContext.subContext(),
                                        ConfigPropertyDef.buildForParameter(parameter, parameter)
                                                .orElseThrow(IllegalStateException::new)
                                )
                                .orElseGet(() -> Result.message(
                                        currentContext.format(
                                                String.format(
                                                        "No parser found for %s",
                                                        parameter.getClass().getCanonicalName()
                                                )
                                        )
                                ))
                )
                .collect(
                        Collectors.collectingAndThen(
                                Collectors.toList(),
                                Result::unwrap
                        )
                );

        Result<Initializer<T>> initializer = parametersResult.map(
                parameters -> (prefix, envProvider) -> Result
                        .unwrap(
                                parameters.stream()
                                        .map(parameter -> parameter.initialize(prefix, envProvider))
                                        .collect(Collectors.toList())
                        )
                        .flatMap(initializedParameters -> {
                            MappingContext initializationContext = new MappingContext(
                                    String.format(
                                            "%s (constructor initializer)",
                                            propertyClass.getCanonicalName()
                                    )
                            );
                            try {
                                return Result.of(
                                        constructor.newInstance(initializedParameters.toArray())
                                );
                            }
                            catch (InstantiationException
                                    | IllegalAccessException
                                    | InvocationTargetException e
                            ) {
                                return Result.<T>message(
                                        initializationContext.format(
                                                "Could not initialize"
                                        ),
                                        e
                                );
                            }
                        })
        );

        return Optional.of(initializer);
    }
}

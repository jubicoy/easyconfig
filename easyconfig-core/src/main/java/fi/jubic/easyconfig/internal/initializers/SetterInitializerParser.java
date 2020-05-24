package fi.jubic.easyconfig.internal.initializers;

import fi.jubic.easyconfig.internal.ConfigPropertyDef;
import fi.jubic.easyconfig.internal.MappingContext;
import fi.jubic.easyconfig.internal.Result;
import fi.jubic.easyconfig.internal.parameter.Mappable;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class SetterInitializerParser implements InitializerParser {
    @Override
    public <T> Optional<Result<Initializer<T>>> parse(
            MappingContext context,
            ConfigPropertyDef propertyDef
    ) {
        Class<?> propertyClass = propertyDef.getPropertyClass();

        //noinspection unchecked
        Constructor<T>[] allConstructors = (Constructor<T>[]) propertyClass.getConstructors();

        List<Method> setters = Stream.of(propertyClass.getMethods())
                .filter(method -> SUPPORTED_PROPERTY_ANNOTATIONS.stream()
                        .map(method::getAnnotation)
                        .anyMatch(Objects::nonNull)
                )
                .filter(method -> method.getReturnType().equals(void.class))
                .filter(method -> method.getParameterCount() == 1)
                .collect(Collectors.toList());

        if (setters.isEmpty()) {
            return Optional.empty();
        }

        Optional<Constructor<T>> optionalDefaultConstructor = Stream.of(allConstructors)
                .filter(constructor -> constructor.getParameterCount() == 0)
                .findFirst();
        if (!optionalDefaultConstructor.isPresent()) {
            return Optional.empty();
        }
        Constructor<T> defaultConstructor = optionalDefaultConstructor.get();

        MappingContext currentContext = context.push(
                String.format(
                        "%s (setter initializer)",
                        propertyClass.getCanonicalName()
                )
        );

        Result<List<Mappable<?>>> parametersResult = setters.stream()
                .map(setter -> {
                    Parameter parameter = setter.getParameters()[0];
                    return PARAMETER_PARSER
                            .parseParameter(
                                    currentContext.subContext(),
                                    ConfigPropertyDef.buildForParameter(parameter, setter)
                                            .orElseThrow(IllegalStateException::new)
                            )
                            .orElseGet(() -> Result.message(
                                    currentContext.format(
                                            String.format(
                                                    "No parsed found for %s",
                                                    parameter.getClass().getCanonicalName()
                                            )
                                    )
                            ));
                })
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
                                            "%s (setter initializer)",
                                            propertyClass.getCanonicalName()
                                    )
                            );

                            T instance;
                            try {
                                instance = defaultConstructor.newInstance();
                            }
                            catch (InstantiationException
                                    | IllegalAccessException
                                    | InvocationTargetException e
                            ) {
                                return Result.<T>message(
                                        initializationContext.format(
                                                "Could not initialize instance"
                                        ),
                                        e
                                );
                            }

                            for (int i = 0; i < initializedParameters.size(); i++) {
                                try {
                                    setters.get(i)
                                            .invoke(
                                                    instance,
                                                    initializedParameters.get(i)
                                            );
                                }
                                catch (IllegalAccessException | InvocationTargetException e) {
                                    return Result.<T>message(
                                            initializationContext.format(
                                                    String.format(
                                                            "Could not invoke setter %s",
                                                            setters.get(i).getName()
                                                    )
                                            ),
                                            e
                                    );
                                }
                            }
                            return Result.of(instance);
                        })
        );
        return Optional.of(initializer);
    }
}

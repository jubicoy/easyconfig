package fi.jubic.easyconfig.internal.initializers;

import fi.jubic.easyconfig.annotations.EasyConfig;
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

class BuilderClassInitializerParser implements InitializerParser {
    @Override
    public <T> Optional<Result<Initializer<T>>> parse(
            MappingContext context,
            ConfigPropertyDef propertyDef
    ) {
        Class<?> propertyClass = propertyDef.getPropertyClass();

        EasyConfig easyConfig = propertyClass.getAnnotation(EasyConfig.class);
        if (easyConfig == null) return Optional.empty();
        if (easyConfig.builder().equals(Void.class)) return Optional.empty();

        Class<?> builderClass = easyConfig.builder();

        MappingContext currentContext = context.push(
                String.format(
                        "%s (builder initializer %s)",
                        propertyClass.getCanonicalName(),
                        builderClass.getCanonicalName()
                )
        );

        Result<Constructor<?>> builderConstructor = Stream.of(builderClass.getConstructors())
                .filter(constructor -> constructor.getParameterCount() == 0)
                .findFirst()
                .map(Result::<Constructor<?>>of)
                .orElseGet(() -> Result.message(
                        currentContext.format("No default constructor found")
                ));


        Result<Method> buildMethod = Stream.of(builderClass.getMethods())
                .filter(method -> method.getParameterCount() == 0)
                .filter(method -> method.getReturnType().equals(propertyClass))
                .findFirst()
                .map(Result::of)
                .orElseGet(() -> Result.message(
                        currentContext.format("No build method found")
                ));

        Result<List<Method>> setters = Optional
                .of(
                        Stream.of(builderClass.getMethods())
                                .filter(method -> SUPPORTED_PROPERTY_ANNOTATIONS.stream()
                                        .map(method::getAnnotation)
                                        .anyMatch(Objects::nonNull)
                                )
                                .filter(method -> method.getReturnType().equals(builderClass))
                                .filter(method -> method.getParameterCount() == 1)
                                .collect(Collectors.toList())
                )
                .filter(foundSetters -> !foundSetters.isEmpty())
                .map(Result::of)
                .orElseGet(() -> Result.message(
                        currentContext.format("No setter methods found")
                ));

        if (
                builderConstructor.hasMessages()
                        || buildMethod.hasMessages()
                        || setters.hasMessages()
        ) {
            return Optional.of(
                    Result.unwrapMessages(builderConstructor, buildMethod, setters)
            );
        }

        Result<List<Mappable<?>>> parametersResult = setters.getValue()
                .stream()
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
                                                    "No parser found for %s",
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
                        .flatMap(initializerParameters -> {
                            MappingContext initializationContext = new MappingContext(
                                    String.format(
                                            "%s (builder initializer %s)",
                                            propertyClass.getCanonicalName(),
                                            builderClass.getCanonicalName()
                                    )
                            );
                            Object builder;
                            try {
                                builder = builderConstructor.getValue().newInstance();
                            }
                            catch (InstantiationException
                                    | IllegalAccessException
                                    | InvocationTargetException e
                            ) {
                                return Result.<T>message(
                                        initializationContext.format(
                                                "Could not initialize the builder"
                                        ),
                                        e
                                );
                            }

                            for (int i = 0; i < initializerParameters.size(); i++) {
                                try {
                                    builder = setters.getValue()
                                            .get(i)
                                            .invoke(
                                                    builder,
                                                    initializerParameters.get(i)
                                            );
                                }
                                catch (IllegalAccessException | InvocationTargetException e) {
                                    return Result.<T>message(
                                            initializationContext.format(
                                                    String.format(
                                                            "Could not invoke setter %s",
                                                            setters.getValue().get(i).getName()
                                                    )

                                            ),
                                            e
                                    );
                                }

                            }

                            try {
                                //noinspection unchecked
                                return Result.of((T) buildMethod.getValue().invoke(builder));
                            }
                            catch (IllegalAccessException | InvocationTargetException e) {
                                return Result.<T>message(
                                        initializationContext.format(
                                                "Could not invoke build method"
                                        ),
                                        e
                                );
                            }
                        })
        );
        return Optional.of(initializer);
    }
}

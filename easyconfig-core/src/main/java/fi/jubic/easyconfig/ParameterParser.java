package fi.jubic.easyconfig;

import fi.jubic.easyconfig.annotations.ConfigProperty;
import fi.jubic.easyconfig.annotations.EasyConfigProperty;
import fi.jubic.easyconfig.annotations.EnvProviderProperty;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

class ParameterParser {
    private final ConfigMapper mapper;
    private final InitializerBuilder initializerBuilder;

    private static List<Class<?>> supportedClasses = Arrays.asList(
            boolean.class,
            Boolean.class,
            long.class,
            Long.class,
            float.class,
            Float.class,
            double.class,
            Double.class,
            String.class,
            int.class,
            Integer.class
    );

    ParameterParser(
            ConfigMapper mapper,
            InitializerBuilder initializerBuilder
    ) {
        this.mapper = mapper;
        this.initializerBuilder = initializerBuilder;
    }

    Optional<MappableParameter> parseParameter(
            Parameter parameter,
            Method method
    ) {
        if (
                Stream.of(method, parameter)
                        .filter(Objects::nonNull)
                        .map(elem -> elem.getAnnotation(EnvProviderProperty.class))
                        .anyMatch(Objects::nonNull)
        ) {
            if (!EnvProvider.class.isAssignableFrom(parameter.getType())) {
                throw new IllegalArgumentException(
                        String.format(
                                "Invalid parameter to EnvProviderProperty %s",
                                parameter.getType().getCanonicalName().toString()
                        )
                );
            }
            return Optional.of(
                    new MappableParameter(
                            method,
                            MappableParameter.Kind.Provider,
                            EnvProvider.class,
                            null,
                            null
                    )
            );
        }

        ConfigPropertyDef property = Stream
                .of(parameter, method)
                .filter(Objects::nonNull)
                .map(elem -> elem.getAnnotation(EasyConfigProperty.class))
                .filter(Objects::nonNull)
                .findFirst()
                .map(ConfigPropertyDef::new)
                .orElseGet(() -> Stream
                        .of(parameter, method)
                        .filter(Objects::nonNull)
                        .map(elem -> elem.getAnnotation(ConfigProperty.class))
                        .filter(Objects::nonNull)
                        .findFirst()
                        .map(ConfigPropertyDef::new)
                        .orElse(null)
                );

        return parseParameter(
                parameter,
                parameter.getType(),
                property,
                method
        );
    }

    private Optional<MappableParameter> parseParameter(
            Parameter parameter,
            Class<?> parameterClass,
            ConfigPropertyDef propertyDef,
            Method method
    ) {
        if (
                parameterClass.equals(boolean.class)
                        || parameterClass.equals(Boolean.class)
        ) {
            return Optional.of(
                    new MappableParameter(
                            method,
                            MappableParameter.Kind.Primitive,
                            parameterClass,
                            propertyDef,
                            Boolean::parseBoolean,
                            mapper
                    )
            );
        }
        if (
                parameterClass.equals(long.class)
                        || parameterClass.equals(Long.class)
        ) {
            return Optional.of(
                    new MappableParameter(
                            method,
                            MappableParameter.Kind.Primitive,
                            parameterClass,
                            propertyDef,
                            str -> {
                                try {
                                    return Long.parseLong(str, 10);
                                }
                                catch (NumberFormatException e) {
                                    throw new InternalMappingException(
                                            "Could not parse " + propertyDef.getValue(),
                                            e
                                    );
                                }
                            },
                            mapper
                    )
            );
        }
        if (
                parameterClass.equals(float.class)
                        || parameterClass.equals(Float.class)
        ) {
            return Optional.of(
                    new MappableParameter(
                            method,
                            MappableParameter.Kind.Primitive,
                            parameterClass,
                            propertyDef,
                            str -> {
                                try {
                                    return Float.parseFloat(str);
                                }
                                catch (NumberFormatException e) {
                                    throw new InternalMappingException(
                                            "Could not parse " + propertyDef.getValue(),
                                            e
                                    );
                                }
                            },
                            mapper
                    )
            );
        }
        if (
                parameterClass.equals(double.class)
                        || parameterClass.equals(Double.class)
        ) {
            return Optional.of(
                    new MappableParameter(
                            method,
                            MappableParameter.Kind.Primitive,
                            parameterClass,
                            propertyDef,
                            str -> {
                                try {
                                    return Double.parseDouble(str);
                                }
                                catch (NumberFormatException e) {
                                    throw new InternalMappingException(
                                            "Could not parse " + propertyDef.getValue(),
                                            e
                                    );
                                }
                            },
                            mapper
                    )
            );
        }
        if (parameterClass.equals(String.class)) {
            return Optional.of(
                    new MappableParameter(
                            method,
                            MappableParameter.Kind.Primitive,
                            parameterClass,
                            propertyDef,
                            str -> str,
                            mapper
                    )
            );
        }
        if (
                parameterClass.isAssignableFrom(int.class)
                        || parameterClass.isAssignableFrom(Integer.class)
        ) {
            return Optional.of(
                    new MappableParameter(
                            method,
                            MappableParameter.Kind.Primitive,
                            parameterClass,
                            propertyDef,
                            str -> {
                                try {
                                    return Integer.parseInt(str, 10);
                                }
                                catch (NumberFormatException e) {
                                    throw new InternalMappingException(
                                            "Could not parse " + propertyDef.getValue(),
                                            e
                                    );
                                }
                            },
                            mapper
                    )
            );
        }

        boolean nestingApplicable = false;
        try {
            initializerBuilder.build(parameterClass);
            nestingApplicable = true;
        }
        catch (InternalMappingException ignored) {
        }
        if (nestingApplicable) {
            return Optional.of(
                    new MappableParameter(
                            method,
                            MappableParameter.Kind.Nested,
                            parameterClass,
                            propertyDef,
                            mapper
                    )
            );
        }

        if (parameterClass.equals(List.class)) {
            Class<?> klass = (Class<?>) ((ParameterizedType) parameter.getParameterizedType())
                    .getActualTypeArguments()[0];

            boolean listNestingApplicable = false;
            try {
                initializerBuilder.build(klass);
                listNestingApplicable = !supportedClasses.contains(klass);
            }
            catch (InternalMappingException ignored) {
            }

            if (listNestingApplicable) {
                // Check if placeholder is present
                if (!propertyDef.getValue().contains("{}")) {
                    throw new IllegalArgumentException(
                            "Missing index placeholder {} in " + propertyDef.getValue()
                    );
                }

                return Optional.of(
                        new MappableParameter(
                                method,
                                MappableParameter.Kind.NestedList,
                                klass,
                                propertyDef,
                                mapper
                        )
                );
            }
            else {
                return parseParameter(null, klass, propertyDef, method)
                        .map(mappable -> new MappableParameter(
                                method,
                                MappableParameter.Kind.PrimitiveList,
                                klass,
                                propertyDef,
                                str -> {
                                    if (str.length() == 0) {
                                        return Collections.emptyList();
                                    }
                                    List<Object> list = new ArrayList<>();
                                    String[] subStrings = str.split(
                                            propertyDef.getListDelimiter()
                                    );
                                    for (String subString : subStrings) {
                                        list.add(mappable.getMapper().apply(subString));
                                    }
                                    return list;
                                },
                                mapper
                        ));
            }
        }

        return Optional.empty();
    }
}

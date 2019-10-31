package fi.jubic.easyconfig;

import fi.jubic.easyconfig.annotations.EasyConfigProperty;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

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
        EasyConfigProperty property = Optional.ofNullable(parameter.getAnnotation(EasyConfigProperty.class))
                .orElseGet(() -> method.getAnnotation(EasyConfigProperty.class));
        assert (property != null);

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
            EasyConfigProperty propertyAnnotation,
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
                            propertyAnnotation,
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
                            propertyAnnotation,
                            str -> {
                                try {
                                    return Long.parseLong(str, 10);
                                } catch (NumberFormatException e) {
                                    throw new InternalMappingException("Could not parse " + propertyAnnotation.value(), e);
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
                            propertyAnnotation,
                            str -> {
                                try {
                                    return Float.parseFloat(str);
                                } catch (NumberFormatException e) {
                                    throw new InternalMappingException("Could not parse " + propertyAnnotation.value(), e);
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
                            propertyAnnotation,
                            str -> {
                                try {
                                    return Double.parseDouble(str);
                                } catch (NumberFormatException e) {
                                    throw new InternalMappingException("Could not parse " + propertyAnnotation.value(), e);
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
                            propertyAnnotation,
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
                            propertyAnnotation,
                            str -> {
                                 try {
                                     return Integer.parseInt(str, 10);
                                 } catch (NumberFormatException e) {
                                     throw new InternalMappingException("Could not parse " + propertyAnnotation.value(), e);
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
        } catch (InternalMappingException ignore) {
        }
        if (nestingApplicable) {
            return Optional.of(
                    new MappableParameter(
                            method,
                            MappableParameter.Kind.Nested,
                            parameterClass,
                            propertyAnnotation,
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
            } catch (InternalMappingException ignore) {
            }

            if (listNestingApplicable) {
                // Check if placeholder is present
                if (!propertyAnnotation.value().contains("{}")) {
                    throw new RuntimeException("Missing index placeholder {} in " + propertyAnnotation.value());
                }

                return Optional.of(
                        new MappableParameter(
                                method,
                                MappableParameter.Kind.NestedList,
                                klass,
                                propertyAnnotation,
                                mapper
                        )
                );
            }
            else {
                return parseParameter(null, klass, propertyAnnotation, method)
                        .map(mappable -> new MappableParameter(
                                method,
                                MappableParameter.Kind.PrimitiveList,
                                klass,
                                propertyAnnotation,
                                str -> {
                                    List<Object> list = new ArrayList<>();
                                    for (String substr : str.split(propertyAnnotation.listDelimiter())) {
                                        list.add(mappable.getMapper().apply(substr));
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

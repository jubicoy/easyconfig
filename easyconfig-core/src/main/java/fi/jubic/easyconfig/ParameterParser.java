package fi.jubic.easyconfig;

import fi.jubic.easyconfig.annontations.EasyConfigProperty;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

class ParameterParser {
    private final ConfigMapper mapper;
    private final InitializerBuilder initializerBuilder;

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
                            propertyAnnotation,
                            Boolean::parseBoolean
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
                            propertyAnnotation,
                            str -> {
                                try {
                                    return Long.parseLong(str, 10);
                                } catch (NumberFormatException e) {
                                    throw new MappingException("Could not parse " + propertyAnnotation.value(), e);
                                }
                            }
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
                            propertyAnnotation,
                            str -> {
                                try {
                                    return Float.parseFloat(str);
                                } catch (NumberFormatException e) {
                                    throw new MappingException("Could not parse " + propertyAnnotation.value(), e);
                                }
                            }

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
                            propertyAnnotation,
                            str -> {
                                try {
                                    return Double.parseDouble(str);
                                } catch (NumberFormatException e) {
                                    throw new MappingException("Could not parse " + propertyAnnotation.value(), e);
                                }
                            }

                    )
            );
        }
        if (parameterClass.equals(String.class)) {
            return Optional.of(
                    new MappableParameter(
                            method,
                            propertyAnnotation,
                            str -> str
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
                            propertyAnnotation,
                            str -> {
                                 try {
                                     return Integer.parseInt(str, 10);
                                 } catch (NumberFormatException e) {
                                     throw new MappingException("Could not parse " + propertyAnnotation.value(), e);
                                 }
                            }
                    )
            );
        }

        boolean nestingApplicable = false;
        try {
            initializerBuilder.build(parameterClass);
            nestingApplicable = true;
        } catch (MappingException ignore) {
        }
        if (nestingApplicable) {
            return Optional.of(
                    new MappableParameter(
                            method,
                            propertyAnnotation,
                            str -> mapper.read(propertyAnnotation.value(), parameterClass)
                    )
            );
        }

        if (parameterClass.equals(List.class)) {
            Class<?> klass = (Class<?>) ((ParameterizedType) parameter.getParameterizedType())
                    .getActualTypeArguments()[0];
            return parseParameter(null, klass, propertyAnnotation, method)
                    .map(mappable -> new MappableParameter(
                            method,
                            propertyAnnotation,
                            str -> {
                                List<Object> list = new ArrayList<>();
                                for (String substr : str.split(propertyAnnotation.listDelimiter())) {
                                    list.add(mappable.getMapper().apply(substr));
                                }
                                return list;
                            }
                    ));
        }

        return Optional.empty();
    }
}

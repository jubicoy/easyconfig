package fi.jubic.easyconfig;

import fi.jubic.easyconfig.annotations.EasyConfigProperty;

import java.lang.reflect.Method;

class MappableParameter {
    private final Method method;
    private final boolean nested;
    private final Class<?> parameterKlass;
    private final EasyConfigProperty configProperty;
    private final MappingFunction<String, Object> mapper;

    MappableParameter(
            Method method,
            boolean nested,
            Class<?> parameterKlass,
            EasyConfigProperty configProperty,
            MappingFunction<String, Object> mapper
    ) {
        this.method = method;
        this.nested = nested;
        this.parameterKlass = parameterKlass;
        this.configProperty = configProperty;
        this.mapper = mapper;
    }

    Method getMethod() {
        return method;
    }

    EasyConfigProperty getConfigProperty() {
        return configProperty;
    }

    MappingFunction<String, Object> getMapper() {
        return mapper;
    }

    String getStringValue(EnvProvider provider) throws InternalMappingException {
        String stringValue = provider
                .getVariable(configProperty.value())
                .orElse(configProperty.defaultValue());

        if (!stringValue.equals(EasyConfigProperty.UNDEFINED_DEFAULT)) {
            return stringValue;
        }

        if (!nested) {
            throw new InternalMappingException(
                    String.format(
                            "Missing parameter %s%s [%s]",
                            provider.prefix(),
                            configProperty.value(),
                            parameterKlass.getSimpleName()
                    )
            );
        }

        return "";
    }
}

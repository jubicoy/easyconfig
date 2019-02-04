package fi.jubic.easyconfig;

import java.lang.reflect.Method;

class MappableParameter {
    private final Method method;
    private final EasyConfigProperty configProperty;
    private final MappingFunction<String, Object> mapper;

    MappableParameter(
            Method method,
            EasyConfigProperty configProperty,
            MappingFunction<String, Object> mapper
    ) {
        this.method = method;
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
}

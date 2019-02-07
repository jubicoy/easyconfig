package fi.jubic.easyconfig;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

class DefaultConstructorInitializer<T> implements Initializer<T> {
    private final Constructor<T> constructor;
    private final List<MappableParameter> parameters;

    DefaultConstructorInitializer(
            Constructor<T> constructor,
            List<MappableParameter> parameters
    ) {
        this.constructor = constructor;
        this.parameters = parameters;
    }

    @Override
    public T initialize(EnvProvider prefixedProvider) throws MappingException {
        try {
            T config = constructor.newInstance();

            for (MappableParameter parameter : parameters) {
                String stringValue = prefixedProvider
                        .getVariable(parameter.getConfigProperty().value())
                        .orElse(parameter.getConfigProperty().defaultValue());

                parameter.getMethod().invoke(
                        config,
                        parameter.getMapper().apply(stringValue)
                );
            }

            return config;
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new MappingException(e);
        }
    }
}

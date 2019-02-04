package fi.jubic.easyconfig;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

class ParameterizedConstructorInitializer<T> implements Initializer<T> {
    private final Constructor<T> constructor;
    private final List<MappableParameter> parameters;

    ParameterizedConstructorInitializer(
            Constructor<T> constructor,
            List<MappableParameter> parameters
    ) {
        this.constructor = constructor;
        this.parameters = parameters;
    }

    @Override
    public T initialize(EnvProvider prefixedProvider) throws MappingException {
        try {
            List<Object> parameterObjects = new ArrayList<>();
            for (MappableParameter parameter : parameters) {
                String stringValue = prefixedProvider
                        .getVariable(parameter.getConfigProperty().value())
                        .orElse(parameter.getConfigProperty().defaultValue());

                parameterObjects.add(parameter.getMapper().apply(stringValue));
            }

            return constructor.newInstance(parameterObjects.toArray());
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new MappingException(e);
        }
    }
}

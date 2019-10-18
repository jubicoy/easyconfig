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
    public T initialize(EnvProvider prefixedProvider) throws InternalMappingException {
        List<Object> parameterObjects = new ArrayList<>();
        List<InternalMappingException> nestedExceptions = new ArrayList<>();

        for (MappableParameter parameter : parameters) {
            try {
                parameterObjects.add(parameter.readAndParse(prefixedProvider));
            } catch (InternalMappingException e) {
                nestedExceptions.add(e);
            }
        }

        if (!nestedExceptions.isEmpty()) throw new InternalMappingException(nestedExceptions);

        try {
            return constructor.newInstance(parameterObjects.toArray());
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new InternalMappingException("Could not initialize an instance using " + constructor.getName(), e);
        }
    }
}

package fi.jubic.easyconfig;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
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
    public T initialize(EnvProvider prefixedProvider) throws InternalMappingException {
        T config;
        try {
            config = constructor.newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new InternalMappingException(
                    "Could not initialize an instance of class using " + constructor.getName(),
                    e
            );
        }

        List<InternalMappingException> nestedExceptions = new ArrayList<>();

        for (MappableParameter parameter : parameters) {
            try {
                parameter.getMethod().invoke(
                        config,
                        parameter.readAndParse(prefixedProvider)
                );
            } catch (InternalMappingException e) {
                nestedExceptions.add(e);
            } catch (IllegalAccessException | InvocationTargetException e) {
                nestedExceptions.add(
                        new InternalMappingException(
                                "Could not invoke setter method " + parameter.getMethod().getName(),
                                e
                        )
                );
            }
        }

        if (!nestedExceptions.isEmpty()) throw new InternalMappingException(nestedExceptions);

        return config;
    }
}

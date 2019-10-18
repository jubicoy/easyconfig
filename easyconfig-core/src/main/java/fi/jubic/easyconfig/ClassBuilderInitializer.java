package fi.jubic.easyconfig;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

class ClassBuilderInitializer<T> implements Initializer<T> {
    private final Class<?> builderKlass;
    private final Method buildMethod;
    private final List<MappableParameter> parameters;

    ClassBuilderInitializer(
            Class<?> builderKlass,
            Method buildMethod,
            List<MappableParameter> parameters
    ) {
        this.builderKlass = builderKlass;
        this.buildMethod = buildMethod;
        this.parameters = parameters;
    }

    @Override
    public T initialize(EnvProvider prefixedProvider) throws InternalMappingException {
        Object builder;
        try {
            builder = builderKlass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new InternalMappingException(
                    "Could not initialize an instance of class " + builderKlass.getCanonicalName(),
                    e
            );
        }

        List<InternalMappingException> nestedExceptions = new ArrayList<>();

        for (MappableParameter parameter : parameters) {
            String stringValue;
            try {
                stringValue = parameter.getStringValue(prefixedProvider);
            } catch (InternalMappingException e) {
                nestedExceptions.add(e);
                continue;
            }

            try {
                builder = parameter.getMethod().invoke(
                        builder,
                        parameter.getMapper().apply(stringValue)
                );
            } catch (IllegalAccessException | InvocationTargetException e) {
                nestedExceptions.add(
                        new InternalMappingException(
                                "Could not invoke builder method " + parameter.getMethod().getName()
                                        + " in class " + builderKlass.getCanonicalName(),
                                e
                        )
                );
            } catch (InternalMappingException e) {
                nestedExceptions.add(e);
            }
        }

        if (!nestedExceptions.isEmpty()) throw new InternalMappingException(nestedExceptions);

        try {
            //noinspection unchecked
            return (T) buildMethod.invoke(builder);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new InternalMappingException(
                    "Could not invoke build method in class " + builderKlass.getCanonicalName(),
                    e
            );
        }

//        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
//            throw new MappingException(e);
//        }
    }
}

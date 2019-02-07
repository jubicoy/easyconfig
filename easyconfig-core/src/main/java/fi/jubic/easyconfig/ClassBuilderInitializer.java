package fi.jubic.easyconfig;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
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
    public T initialize(EnvProvider prefixedProvider) throws MappingException {
        try {
            Object builder = builderKlass.newInstance();

            for (MappableParameter parameter : parameters) {
                String stringValue = prefixedProvider
                        .getVariable(parameter.getConfigProperty().value())
                        .orElse(parameter.getConfigProperty().defaultValue());

                builder = parameter.getMethod().invoke(
                        builder,
                        parameter.getMapper().apply(stringValue)
                );
            }

            //noinspection unchecked
            return (T) buildMethod.invoke(builder);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new MappingException(e);
        }
    }
}

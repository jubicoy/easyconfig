package fi.jubic.easyconfig;

import fi.jubic.easyconfig.annotations.ConfigProperty;

import java.lang.reflect.Method;
import java.util.stream.Collectors;

class MappableParameter {
    enum Kind {
        Primitive,
        PrimitiveList,
        Nested,
        NestedList,
        Provider
    }

    private final Method method;
    private final Kind kind;
    private final Class<?> parameterKlass;
    private final ConfigPropertyDef configProperty;
    private final MappingFunction<String, Object> mapper;

    MappableParameter(
            Method method,
            Kind kind,
            Class<?> parameterKlass,
            ConfigPropertyDef configProperty,
            ConfigMapper configMapper
    ) {
        this(
                method,
                kind,
                parameterKlass,
                configProperty,
                str -> "",
                configMapper
        );
    }

    MappableParameter(
            Method method,
            Kind kind,
            Class<?> parameterKlass,
            ConfigPropertyDef configProperty,
            MappingFunction<String, Object> mapper,
            ConfigMapper configMapper
    ) {
        this.method = method;
        this.kind = kind;
        this.parameterKlass = parameterKlass;
        this.configProperty = configProperty;
        this.mapper = mapper;
    }

    Method getMethod() {
        return method;
    }

    MappingFunction<String, Object> getMapper() {
        return mapper;
    }

    Object readAndParse(EnvProvider provider) throws InternalMappingException {
        switch (kind) {
            case Provider:
                return provider;

            case Nested:
                return new ConfigMapper(provider)
                        .internalRead(
                                configProperty.getValue(),
                                parameterKlass
                        );

            case NestedList:
                try {
                    ConfigMapper prefixedMapper = new ConfigMapper(provider);
                    return provider.getKeysMatching(configProperty.getValue())
                            .map(listKey -> {
                                try {
                                    return prefixedMapper.internalRead(
                                            configProperty.getValue().replace("{}", listKey),
                                            parameterKlass
                                    );
                                }
                                catch (InternalMappingException e) {
                                    throw new RuntimeException(e);
                                }
                            })
                            .collect(Collectors.toList());
                }
                catch (RuntimeException e) {
                    throw (InternalMappingException) e.getCause();
                }

            default:
                return mapper.apply(getStringValue(provider));
        }
    }

    private String getStringValue(EnvProvider provider) throws InternalMappingException {
        String stringValue = provider
                .getVariable(configProperty.getValue())
                .orElse(configProperty.getDefaultValue());

        if (!stringValue.equals(ConfigProperty.UNDEFINED_DEFAULT)) {
            return stringValue;
        }

        if (kind.equals(Kind.Primitive) || kind.equals(Kind.PrimitiveList)) {
            throw new InternalMappingException(
                    String.format(
                            "Missing parameter %s%s [%s]",
                            provider.prefix(),
                            configProperty.getValue(),
                            parameterKlass.getSimpleName()
                    )
            );
        }

        return "";
    }
}

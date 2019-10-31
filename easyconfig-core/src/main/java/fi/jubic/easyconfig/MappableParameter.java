package fi.jubic.easyconfig;

import fi.jubic.easyconfig.annotations.EasyConfigProperty;

import java.lang.reflect.Method;
import java.util.stream.Collectors;

class MappableParameter {
    enum Kind {
        Primitive,
        PrimitiveList,
        Nested,
        NestedList
    }

    private final Method method;
    private final Kind kind;
    private final Class<?> parameterKlass;
    private final EasyConfigProperty configProperty;
    private final MappingFunction<String, Object> mapper;
    private final ConfigMapper configMapper;

    MappableParameter(
            Method method,
            Kind kind,
            Class<?> parameterKlass,
            EasyConfigProperty configProperty,
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
            EasyConfigProperty configProperty,
            MappingFunction<String, Object> mapper,
            ConfigMapper configMapper
    ) {
        this.method = method;
        this.kind = kind;
        this.parameterKlass = parameterKlass;
        this.configProperty = configProperty;
        this.mapper = mapper;
        this.configMapper = configMapper;
    }

    Method getMethod() {
        return method;
    }

    MappingFunction<String, Object> getMapper() {
        return mapper;
    }

    Object readAndParse(EnvProvider provider) throws InternalMappingException {
        if (kind.equals(Kind.Nested)) {
            return new ConfigMapper(provider).internalRead(configProperty.value(), parameterKlass);
        }
        if (kind.equals(Kind.NestedList)) {
            try {
                ConfigMapper prefixedMapper = new ConfigMapper(provider);
                return provider.getKeysMatching(configProperty.value())
                        .map(listKey -> {
                            try {
                                return prefixedMapper.internalRead(
                                        configProperty.value().replace("{}", listKey),
                                        parameterKlass
                                );
                            } catch (InternalMappingException e) {
                                throw new RuntimeException(e);
                            }
                        })
                        .collect(Collectors.toList());
            } catch (RuntimeException e) {
                throw (InternalMappingException) e.getCause();
            }
        }
        return mapper.apply(getStringValue(provider));
    }

    private String getStringValue(EnvProvider provider) throws InternalMappingException {
        String stringValue = provider
                .getVariable(configProperty.value())
                .orElse(configProperty.defaultValue());

        if (!stringValue.equals(EasyConfigProperty.UNDEFINED_DEFAULT)) {
            return stringValue;
        }

        if (kind.equals(Kind.Primitive) || kind.equals(Kind.PrimitiveList)) {
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

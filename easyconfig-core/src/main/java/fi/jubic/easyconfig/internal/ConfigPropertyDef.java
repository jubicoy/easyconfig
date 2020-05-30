package fi.jubic.easyconfig.internal;

import fi.jubic.easyconfig.annotations.ConfigProperty;
import fi.jubic.easyconfig.annotations.EasyConfigProperty;
import fi.jubic.easyconfig.annotations.EnvProviderProperty;
import fi.jubic.easyconfig.extensions.ConfigExtension;
import fi.jubic.easyconfig.extensions.ConfigExtensionProvider;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Internal property representation for supporting the deprecated
 * {@link fi.jubic.easyconfig.annotations.EasyConfigProperty} annotation.
 */
@SuppressWarnings("deprecation")
public class ConfigPropertyDef {
    private final String variableName;
    private final String defaultValue;
    private final String listDelimiter;
    private final boolean nullable;
    private final boolean noPrefix;

    private final Class<?> propertyClass;
    private final List<Class<?>> typeArguments;
    private final AnnotatedElement annotatedElement;

    private ConfigPropertyDef(
            EasyConfigProperty property,
            Class<?> propertyClass,
            List<Class<?>> typeArguments,
            AnnotatedElement annotatedElement
    ) {
        this.variableName = property.value();
        this.defaultValue = parseDefaultValue(property.defaultValue());
        this.listDelimiter = property.listDelimiter();
        this.nullable = false;
        this.noPrefix = false;

        this.propertyClass = propertyClass;
        this.typeArguments = typeArguments;
        this.annotatedElement = annotatedElement;
    }

    private ConfigPropertyDef(
            ConfigProperty property,
            Class<?> propertyClass,
            List<Class<?>> typeArguments,
            AnnotatedElement annotatedElement
    ) {
        this.variableName = property.value();
        this.defaultValue = property.defaultValue();
        this.listDelimiter = property.listDelimiter();
        this.nullable = property.nullable();
        this.noPrefix = property.noPrefix();

        this.propertyClass = propertyClass;
        this.typeArguments = typeArguments;
        this.annotatedElement = annotatedElement;
    }

    private ConfigPropertyDef(
            Class<?> propertyClass,
            List<Class<?>> typeArguments,
            AnnotatedElement annotatedElement
    ) {
        this.variableName = null;
        this.defaultValue = null;
        this.listDelimiter = null;
        this.nullable = false;
        this.noPrefix = false;

        this.propertyClass = propertyClass;
        this.typeArguments = typeArguments;
        this.annotatedElement = annotatedElement;
    }

    /**
     * Create property definition from a {@code Class}.
     *
     * @param prefix the variable namespace prefix
     * @param propertyClass the class of the property
     */
    public ConfigPropertyDef(
            String prefix,
            Class<?> propertyClass
    ) {
        this.variableName = prefix;
        this.defaultValue = null;
        this.listDelimiter = null;
        this.nullable = false;
        this.noPrefix = false;

        this.propertyClass = propertyClass;
        this.typeArguments = Collections.emptyList();
        this.annotatedElement = null;
    }

    public static ConfigPropertyDef buildForExtensionProvider(
            Annotation annotation
    ) {
        ConfigExtension configExtension = annotation.annotationType()
                .getAnnotation(ConfigExtension.class);
        if (configExtension == null) {
            throw new IllegalStateException(
                    String.format(
                            "Invalid extension %s: %s annotation not found",
                            annotation.annotationType().getCanonicalName(),
                            ConfigExtension.class.getSimpleName()
                    )
            );
        }

        Class<? extends ConfigExtensionProvider<? extends Annotation, ?>> providerClass
                = configExtension.value();

        return new ConfigPropertyDef(
                "",
                providerClass
        );
    }

    public static Optional<ConfigPropertyDef> buildForParameter(
            Parameter parameter,
            AnnotatedElement element
    ) {
        return Stream
                .of(
                        Optional.ofNullable(
                                element.getAnnotation(ConfigProperty.class)
                        ).map(annotation -> new ConfigPropertyDef(
                                annotation,
                                classOf(parameter),
                                typeArgsOf(parameter),
                                element
                        )),
                        Optional.ofNullable(
                                element.getAnnotation(EasyConfigProperty.class)
                        ).map(annotation -> new ConfigPropertyDef(
                                annotation,
                                classOf(parameter),
                                typeArgsOf(parameter),
                                element
                        )),
                        Optional.ofNullable(
                                element.getAnnotation(EnvProviderProperty.class)
                        ).map(annotation -> new ConfigPropertyDef(
                                classOf(parameter),
                                typeArgsOf(parameter),
                                element

                        ))
                )
                .filter(Optional::isPresent)
                .map(Optional::get)
                .findFirst();
    }

    public String getVariableName() {
        return variableName;
    }

    public Optional<String> getDefaultValue() {
        return Optional.ofNullable(defaultValue)
                .filter(defaultValue -> !ConfigProperty.UNDEFINED_DEFAULT.equals(defaultValue));
    }

    public String getListDelimiter() {
        return listDelimiter;
    }

    public boolean isNullable() {
        return nullable;
    }

    public boolean isNoPrefix() {
        return noPrefix;
    }

    public Class<?> getPropertyClass() {
        return propertyClass;
    }

    public List<Class<?>> getTypeArguments() {
        return typeArguments;
    }

    public Optional<AnnotatedElement> getAnnotatedElement() {
        return Optional.ofNullable(annotatedElement);
    }

    private String parseDefaultValue(String defaultValue) {
        if (defaultValue.equals(ConfigProperty.UNDEFINED_DEFAULT)) {
            return null;
        }
        return defaultValue;
    }

    private static Class<?> classOf(Parameter parameter) {
        return parameter.getType();
    }

    private static List<Class<?>> typeArgsOf(Parameter parameter) {
        Optional<List<Class<?>>> typeArgs = Optional
                .ofNullable(parameter.getParameterizedType())
                .filter(type -> type instanceof ParameterizedType)
                .map(type -> (ParameterizedType) type)
                .map(type -> Stream.of(type.getActualTypeArguments())
                        .map(argument -> (Class<?>) argument)
                        .collect(Collectors.toList())
                );

        return typeArgs.orElseGet(Collections::emptyList);
    }
}

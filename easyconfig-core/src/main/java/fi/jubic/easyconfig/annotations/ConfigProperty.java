package fi.jubic.easyconfig.annotations;

import fi.jubic.easyconfig.providers.EnvProvider;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * An annotation for marking and configuring configuration properties. This annotation ca be
 * applied to constructor parameters, setters and builder class setters. Unmarked members are
 * ignored.
 */
@Target({ElementType.METHOD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ConfigProperty {
    /**
     * Environment variable name providing this property or the prefix used to resolve properties
     * of a nested class.
     *
     * <p>
     *     For example, {@code ConfigProperty("HOST")} on a {@code String} property results in an
     *     environment variable named {@code "HOST"} being read and assigned to the property.
     *     {@code ConfigProperty("DB_")} on a nested database config property results in properties
     *     of the database config class being read using the {@code "DB_"} prefix on lookup.
     * </p>
     */
    String value();

    /**
     * Default value as a {@code String}. The parameter parser is applied on the {@code String} in
     * the same way as it would be applied to a value acquired from an
     * {@link EnvProvider}. If the default value is not defined, an
     * {@link IllegalArgumentException} is thrown if corresponding environment variable is not
     * set.
     *
     * <p>
     *     Default values can be applied only to primitive properties and primitive lists.
     *     Primitive lists can use an empty string to default to an empty list.
     * </p>
     *
     * <p>{@code defaultValue} cannot be used together with {@link ConfigProperty#nullable()}.</p>
     */
    String defaultValue() default UNDEFINED_DEFAULT;

    /**
     * Class representations of primitive values can be defined as nullable. This applies to:
     *
     * <ul>
     *     <li>{@link Boolean}</li>
     *     <li>{@link Integer}</li>
     *     <li>{@link Long}</li>
     *     <li>{@link Float}</li>
     *     <li>{@link Double}</li>
     *     <li>{@link String}</li>
     * </ul>
     *
     *
     * <p>{@code nullable} cannot be used together with {@link ConfigProperty#defaultValue()}.</p>
     *
     * @return true if the property is nullable
     */
    boolean nullable() default false;

    /**
     * Delimiter used for primitive lists.
     */
    String listDelimiter() default ";";

    String UNDEFINED_DEFAULT = "no-default-fc29b1a2-48f8-4c7b-8ed3-3df3629f84db";
}

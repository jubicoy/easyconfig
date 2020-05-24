package fi.jubic.easyconfig.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * An annotation for marking and configuring properties.
 *
 * @deprecated Use {@link ConfigProperty} instead.
 */
@SuppressWarnings("DeprecatedIsStillUsed")
@Target({ElementType.METHOD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Deprecated
public @interface EasyConfigProperty {
    /**
     * Name of primitive value of prefix of an Object value.
     */
    String value();

    /**
     * Default value as a string. Parser is applied on the string in the same way as
     * it would be applied to a value acquired from an EnvProvider. If default value
     * is not defined, exception is thrown if corresponding environment variable is
     * not found. Primitive lists can use an empty string to default to an empty list.
     */
    String defaultValue() default UNDEFINED_DEFAULT;

    /**
     * Delimiter used for primitive lists.
     */
    String listDelimiter() default ";";

    String UNDEFINED_DEFAULT = "no-default-fc29b1a2-48f8-4c7b-8ed3-3df3629f84db";
}

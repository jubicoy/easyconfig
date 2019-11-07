package fi.jubic.easyconfig.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface EasyConfigProperty {
    String value();

    String defaultValue() default UNDEFINED_DEFAULT;

    String listDelimiter() default ";";

    String UNDEFINED_DEFAULT = "no-default-fc29b1a2-48f8-4c7b-8ed3-3df3629f84db";
}

package fi.jubic.easyconfig.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface EasyConfig {
    /**
     * Prefix value is ignored.
     */
    @Deprecated
    String prefix() default "";
    
    Class<?> builder() default Void.class;
}

package fi.jubic.easyconfig.extensions;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.ANNOTATION_TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ConfigExtension {
    Class<? extends ConfigExtensionProvider<? extends Annotation, ?>> value();
}

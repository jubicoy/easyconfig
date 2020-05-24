package fi.jubic.easyconfig.extensions;

import fi.jubic.easyconfig.dbunit.DbUnitExtensionProvider;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@ConfigExtension(DbUnitExtensionProvider.class)
@Retention(RetentionPolicy.RUNTIME)
public @interface DbUnitExtension {
    String dataset();

    String dtd() default "";
}

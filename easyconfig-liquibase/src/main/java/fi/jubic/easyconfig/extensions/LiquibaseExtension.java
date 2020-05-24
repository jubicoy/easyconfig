package fi.jubic.easyconfig.extensions;

import fi.jubic.easyconfig.liquibase.LiquibaseExtensionProvider;

import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@ConfigExtension(LiquibaseExtensionProvider.class)
@Retention(RetentionPolicy.RUNTIME)
public @interface LiquibaseExtension {
    String migrations();

    LiquibaseExtension DEFAULT = new LiquibaseExtension() {
        @Override
        public String migrations() {
            return "migrations.xml";
        }

        @Override
        public Class<? extends Annotation> annotationType() {
            return LiquibaseExtension.class;
        }
    };
}

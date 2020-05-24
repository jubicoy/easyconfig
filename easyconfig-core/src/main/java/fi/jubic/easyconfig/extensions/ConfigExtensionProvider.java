package fi.jubic.easyconfig.extensions;

import java.lang.annotation.Annotation;

public interface ConfigExtensionProvider<C extends Annotation, T> {
    T extend(C extensionParams, T configuration);
}

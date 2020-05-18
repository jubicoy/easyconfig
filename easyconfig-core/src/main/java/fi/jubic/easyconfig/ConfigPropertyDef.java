package fi.jubic.easyconfig;

import fi.jubic.easyconfig.annotations.ConfigProperty;
import fi.jubic.easyconfig.annotations.EasyConfigProperty;

/**
 * Internal property representation for supporting the deprecated
 * {@link fi.jubic.easyconfig.annotations.EasyConfigProperty} annotation.
 */
class ConfigPropertyDef {
    private final String value;
    private final String defaultValue;
    private final String listDelimiter;

    ConfigPropertyDef(EasyConfigProperty property) {
        this.value = property.value();
        this.defaultValue = property.defaultValue();
        this.listDelimiter = property.listDelimiter();
    }

    ConfigPropertyDef(ConfigProperty property) {
        this.value = property.value();
        this.defaultValue = property.defaultValue();
        this.listDelimiter = property.listDelimiter();
    }

    public String getValue() {
        return value;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public String getListDelimiter() {
        return listDelimiter;
    }
}

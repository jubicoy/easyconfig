package fi.jubic.easyconfig.logback;

import fi.jubic.easyconfig.annotations.ConfigProperty;

public class NamedLoggerDefinition {
    private final String name;
    private final LoggerDefinition loggerDefinition;


    public NamedLoggerDefinition(
            @ConfigProperty("NAME") String name,
            @ConfigProperty("") LoggerDefinition loggerDefinition
    ) {
        this.name = name;
        this.loggerDefinition = loggerDefinition;
    }

    public String getName() {
        return name;
    }

    public LoggerDefinition getLoggerDefinition() {
        return loggerDefinition;
    }
}

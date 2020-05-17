package fi.jubic.easyconfig.logback;

import ch.qos.logback.classic.Level;
import fi.jubic.easyconfig.annotations.ConfigProperty;

import java.util.List;
import java.util.Optional;

public class LoggerDefinition {
    private final Level level;
    private final boolean additive;
    private final List<String> appenderRefs;

    public LoggerDefinition(
            @ConfigProperty(value = "LEVEL", defaultValue = "") String level,
            @ConfigProperty(value = "ADDITIVE", defaultValue = "true")
                    boolean additive,
            @ConfigProperty(
                    value = "APPENDER_REFS",
                    defaultValue = ""
            ) List<String> appenderRefs
    ) {
        if (level.length() == 0) {
            this.level = null;
        }
        else {
            this.level = Level.valueOf(level);
            if (!this.level.levelStr.equals(level)) {
                throw new IllegalArgumentException(
                        String.format(
                                "Invalid log level %s",
                                level
                        )
                );
            }
        }

        this.additive = additive;
        this.appenderRefs = appenderRefs;
    }

    public Optional<Level> getLevel() {
        return Optional.ofNullable(level);
    }

    public boolean isAdditive() {
        return additive;
    }

    public List<String> getAppenderRefs() {
        return appenderRefs;
    }
}

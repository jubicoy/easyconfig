package fi.jubic.easyconfig.logback;

import ch.qos.logback.classic.Level;
import fi.jubic.easyconfig.annotations.ConfigProperty;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class LoggerDefinition {
    private final Level level;
    private final Boolean additive;
    private final List<String> appenderRefs;

    public LoggerDefinition(
            @ConfigProperty(
                    value = "LEVEL",
                    nullable = true
            ) String levelStr,
            @ConfigProperty(
                    value = "ADDITIVE",
                    nullable = true
            ) Boolean additive,
            @ConfigProperty(
                    value = "APPENDER_REFS",
                    defaultValue = ""
            ) List<String> appenderRefs
    ) {
        this.level = Optional.ofNullable(levelStr)
                .filter(level -> level.length() > 0)
                .map(level -> {
                    Level parsedLevel = Level.valueOf(level);
                    if (!level.equals(parsedLevel.toString())) {
                        throw new IllegalArgumentException(
                                String.format(
                                        "Invalid log level %s",
                                        level
                                )
                        );
                    }
                    return parsedLevel;
                })
                .orElse(null);

        this.additive = additive;
        this.appenderRefs = Collections.unmodifiableList(appenderRefs);
    }

    public Optional<Level> getLevel() {
        return Optional.ofNullable(level);
    }

    public Optional<Boolean> isAdditive() {
        return Optional.ofNullable(additive);
    }

    public List<String> getAppenderRefs() {
        return Collections.unmodifiableList(appenderRefs);
    }
}

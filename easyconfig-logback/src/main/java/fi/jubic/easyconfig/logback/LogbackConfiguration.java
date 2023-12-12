package fi.jubic.easyconfig.logback;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.util.DefaultJoranConfigurator;
import ch.qos.logback.core.Appender;
import fi.jubic.easyconfig.annotations.ConfigProperty;
import fi.jubic.easyconfig.annotations.EnvProviderProperty;
import fi.jubic.easyconfig.providers.EnvProvider;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class LogbackConfiguration {
    public LogbackConfiguration(
            @EnvProviderProperty EnvProvider envProvider,
            @ConfigProperty(
                    value = "LOGBACK_ROOT_"
            ) LoggerDefinition root,
            @ConfigProperty(
                    value = "LOGBACK_LOGGER_{}_",
                    defaultValue = ""
            ) List<NamedLoggerDefinition> loggers
    ) {
        var loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();

        loggerContext.reset();

        envProvider.getAdditionalVariables()
                .forEach(loggerContext::putProperty);

        loggerContext.putProperty("DEPLOYMENT_ENVIRONMENT", "development");

        var configurator = new DefaultJoranConfigurator();
        configurator.setContext(loggerContext);
        configurator.configure(loggerContext);

        var appenderMap = getAppenderMap(loggerContext);

        if (root != null) {
            var rootLogger = loggerContext.getLogger(Logger.ROOT_LOGGER_NAME);
            setupLogger(rootLogger, root, appenderMap);
        }

        loggers.forEach(logger -> setupLogger(
                loggerContext.getLogger(logger.getName()),
                logger.getLoggerDefinition(),
                appenderMap
        ));
    }

    private void setupLogger(
            Logger logger,
            LoggerDefinition loggerDefinition,
            Map<String, Appender<ILoggingEvent>> appenderMap
    ) {
        loggerDefinition.getLevel().ifPresent(logger::setLevel);
        loggerDefinition.isAdditive().ifPresent(logger::setAdditive);

        Set<String> attachedAppenderNames = getAppenderNames(logger);
        appenderMap.forEach((name, appender) -> {
            if (!attachedAppenderNames.contains(name)) {
                logger.addAppender(appender);
            }
        });
    }

    private Map<String, Appender<ILoggingEvent>> getAppenderMap(
            LoggerContext loggerContext
    ) {
        return loggerContext.getLoggerList()
                .stream()
                .flatMap(logger -> StreamSupport
                        .stream(
                                Spliterators.spliteratorUnknownSize(
                                        logger.iteratorForAppenders(),
                                        Spliterator.ORDERED
                                ),
                                false
                        )
                )
                .collect(Collectors.toMap(
                        Appender::getName,
                        Function.identity(),
                        (a, b) -> a
                ));
    }

    private Set<String> getAppenderNames(Logger logger) {
        return StreamSupport
                .stream(
                        Spliterators.spliteratorUnknownSize(
                                logger.iteratorForAppenders(),
                                Spliterator.ORDERED
                        ),
                        false
                )
                .map(Appender::getName)
                .collect(Collectors.toSet());
    }
}

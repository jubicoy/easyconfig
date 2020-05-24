package fi.jubic.easyconfig.logback;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.util.ContextInitializer;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.joran.spi.JoranException;
import fi.jubic.easyconfig.annotations.ConfigProperty;
import fi.jubic.easyconfig.annotations.EnvProviderProperty;
import fi.jubic.easyconfig.providers.EnvProvider;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class LogbackConfig {
    public LogbackConfig(
            @EnvProviderProperty EnvProvider envProvider,
            @ConfigProperty(
                    value = "LOGBACK_ROOT_"
            ) LoggerDefinition root,
            @ConfigProperty(
                    value = "LOGBACK_LOGGER_{}_",
                    defaultValue = ""
            ) List<NamedLoggerDefinition> loggers
    ) {
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();

        ContextInitializer ci = new ContextInitializer(loggerContext);
        URL url = ci.findURLOfDefaultConfigurationFile(true);

        loggerContext.reset();

        envProvider.getAdditionalVariables()
                .forEach(loggerContext::putProperty);

        loggerContext.putProperty("DEPLOYMENT_ENVIRONMENT", "development");

        JoranConfigurator configurator = new JoranConfigurator();
        configurator.setContext(loggerContext);

        try {
            configurator.doConfigure(url);
        }
        catch (JoranException e) {
            throw new IllegalStateException(e);
        }

        Map<String, Appender<ILoggingEvent>> appenderMap = getAppenderMap(loggerContext);

        if (root != null) {
            Logger rootLogger = loggerContext.getLogger(Logger.ROOT_LOGGER_NAME);
            rootLogger.iteratorForAppenders();
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

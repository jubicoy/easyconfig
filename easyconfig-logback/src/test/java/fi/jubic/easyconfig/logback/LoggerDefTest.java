package fi.jubic.easyconfig.logback;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import fi.jubic.easyconfig.ConfigMapper;
import fi.jubic.easyconfig.MappingException;
import fi.jubic.easyconfig.StaticEnvProvider;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LoggerDefTest {
    @Test
    void shouldProvideNonSystemEnvironmentVariablesToConfig() throws MappingException {
        {
            new ConfigMapper(new StaticEnvProvider()).read(LogbackConfig.class);

            Logger logger = (Logger) LoggerFactory.getLogger("logger-b");
            assertEquals(logger.getLevel(), Level.INFO);
        }

        {
            new ConfigMapper(
                    new StaticEnvProvider().with("EXTERNAL_LEVEL", "DEBUG")
            ).read(LogbackConfig.class);

            Logger logger = (Logger) LoggerFactory.getLogger("logger-b");
            assertEquals(logger.getLevel(), Level.DEBUG);
        }
    }

    @Test
    void shouldAddNewLoggerWithCustomAppender() throws MappingException {
        new ConfigMapper(
                new StaticEnvProvider()
                        .with("LOGBACK_LOGGER_0_NAME", "added")
                        .with("LOGBACK_LOGGER_0_LEVEL", "DEBUG")
                        .with("LOGBACK_LOGGER_0_APPENDER_REFS", "STDOUT;custom")
        ).read(LogbackConfig.class);

        Logger logger = (Logger) LoggerFactory.getLogger("added");
        assertEquals(Level.DEBUG, logger.getLevel());
        assertTrue(logger.isAdditive());

        Set<String> appenders = new HashSet<>();
        Iterator<Appender<ILoggingEvent>> iterator = logger.iteratorForAppenders();
        while (iterator.hasNext()) {
            appenders.add(iterator.next().getName());
        }

        assertEquals(2, appenders.size());
        assertTrue(appenders.contains("STDOUT"));
        assertTrue(appenders.contains("custom"));
    }

    @Test
    void shouldAllowChangingRootLogLevel() throws MappingException {
        {
            Logger logger = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
            assertEquals(Level.WARN, logger.getLevel());
        }

        new ConfigMapper(
                new StaticEnvProvider().with("LOGBACK_ROOT_LEVEL", "DEBUG")
        ).read(LogbackConfig.class);

        {
            Logger logger = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
            assertEquals(Level.DEBUG, logger.getLevel());
        }
    }

    @Test
    void shouldAllowChangingNamedLoggerLogLevel() throws MappingException {
        {
            Logger logger = (Logger) LoggerFactory.getLogger("logger-a");
            assertEquals(Level.WARN, logger.getLevel());
        }

        new ConfigMapper(
                new StaticEnvProvider()
                        .with("LOGBACK_LOGGER_0_NAME", "logger-a")
                        .with("LOGBACK_LOGGER_0_LEVEL", "INFO")
        ).read(LogbackConfig.class);

        {
            Logger logger = (Logger) LoggerFactory.getLogger("logger-a");
            assertEquals(Level.INFO, logger.getLevel());
        }
    }
}

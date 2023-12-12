package fi.jubic.easyconfig.jdbc;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import fi.jubic.easyconfig.ConfigMapper;
import fi.jubic.easyconfig.providers.EnvProvider;
import fi.jubic.easyconfig.providers.StaticEnvProvider;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class DuplicateConfigWarningTest {
    @Test
    void warnAboutDuplicateConfigs() {
        var logger = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(
                Logger.ROOT_LOGGER_NAME
        );
        //noinspection unchecked
        var appender = (Appender<ILoggingEvent>) mock(Appender.class);

        logger.addAppender(appender);

        new ConfigMapper(envProvider1)
                .read(PooledJdbcConfiguration.class);
        new ConfigMapper(envProvider2)
                .read(PooledJdbcConfiguration.class);

        verify(appender, never()).doAppend(any());

        new ConfigMapper(envProvider1)
                .read(PooledJdbcConfiguration.class);

        var captor = ArgumentCaptor.forClass(ILoggingEvent.class);

        verify(appender, times(1)).doAppend(captor.capture());

        assertEquals(
                Level.WARN.toString(),
                captor.getValue().getLevel().toString()
        );
        assertEquals(
                "Multiple connection pools initialized with the same connection "
                        + "parameters to jdbc:h2:./target/tmp/dup-test-1. Make sure a singleton "
                        + "configuration is used.",
                captor.getValue().getFormattedMessage()
        );
    }

    private static final EnvProvider envProvider1 = new StaticEnvProvider()
            .with("URL", "jdbc:h2:./target/tmp/dup-test-1")
            .with("USER", "SA")
            .with("PASSWORD", "")
            .with("DIALECT", "H2");

    // Slightly different url
    private final EnvProvider envProvider2 = new StaticEnvProvider()
            .with("URL", "jdbc:h2:./target/tmp/dup-test-2")
            .with("USER", "SA")
            .with("PASSWORD", "")
            .with("DIALECT", "H2");
}

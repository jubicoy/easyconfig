package fi.jubic.easyconfig.jooq;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;
import fi.jubic.easyconfig.ConfigMapper;
import fi.jubic.easyconfig.EnvProvider;
import fi.jubic.easyconfig.MappingException;
import fi.jubic.easyconfig.StaticEnvProvider;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DuplicateConfigWarningTest {
    @Test
    void warnAboutDuplicateConfigs() throws MappingException {
        ch.qos.logback.classic.Logger logger = (ch.qos.logback.classic.Logger)LoggerFactory
                .getLogger(JooqConfiguration.class);

        TestAppender appender = new TestAppender();
        appender.setContext(logger.getLoggerContext());
        appender.start();
        logger.addAppender(appender);

        TestAppender.events.clear();

        new ConfigMapper(envProvider1)
                .read(JooqConfiguration.class);
        new ConfigMapper(envProvider2)
                .read(JooqConfiguration.class);

        assertEquals(
                0,
                TestAppender.events.size()
        );

        new ConfigMapper(envProvider1)
                .read(JooqConfiguration.class);

        assertEquals(
                1,
                TestAppender.events.size()
        );
        assertEquals(
                Level.WARN.toString(),
                TestAppender.events.get(0).getLevel().toString()
        );
    }

    private static EnvProvider envProvider1 = new StaticEnvProvider() {{
        put("JOOQ_URL", "jdbc:h2:/tmp/dup-test-1");
        put("JOOQ_USER", "SA");
        put("JOOQ_PASSWORD", "");
        put("JOOQ_DIALECT", "H2");
    }};

    // Slightly different url
    private EnvProvider envProvider2 = new StaticEnvProvider() {{
        put("JOOQ_URL", "jdbc:h2:/tmp/dup-test-2");
        put("JOOQ_USER", "SA");
        put("JOOQ_PASSWORD", "");
        put("JOOQ_DIALECT", "H2");
    }};

    static class TestAppender extends AppenderBase<ILoggingEvent> {
        static List<ILoggingEvent> events = new ArrayList<>();

        @Override
        public String getName() {
            return "TestAppender";
        }

        @Override
        protected void append(ILoggingEvent loggingEvent) {
            events.add(loggingEvent);
        }
    }
}

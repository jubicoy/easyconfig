package fi.jubic.easyconfig.jooq;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;
import fi.jubic.easyconfig.ConfigMapper;
import fi.jubic.easyconfig.EnvProvider;
import fi.jubic.easyconfig.MappingException;
import fi.jubic.easyconfig.StaticEnvProvider;

import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class DuplicateConfigWarningTest {
    @Test
    public void warnAboutDuplicateConfigs() throws MappingException {
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

        assertThat(
                TestAppender.events.size(),
                is(0)
        );

        new ConfigMapper(envProvider1)
                .read(JooqConfiguration.class);

        assertThat(
                TestAppender.events.size(),
                is(1)
        );
        assertThat(
                TestAppender.events.get(0).getLevel().toString(),
                is(Level.WARN.toString())
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

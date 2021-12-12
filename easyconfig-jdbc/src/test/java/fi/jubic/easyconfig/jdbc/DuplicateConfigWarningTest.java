package fi.jubic.easyconfig.jdbc;

import fi.jubic.easyconfig.ConfigMapper;
import fi.jubic.easyconfig.providers.EnvProvider;
import fi.jubic.easyconfig.providers.StaticEnvProvider;
import org.junit.jupiter.api.Test;
import org.slf4j.event.Level;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DuplicateConfigWarningTest {
    @Test
    void warnAboutDuplicateConfigs() {
        TestAppender.events.clear();

        new ConfigMapper(envProvider1)
                .read(PooledJdbcConfiguration.class);
        new ConfigMapper(envProvider2)
                .read(PooledJdbcConfiguration.class);

        assertEquals(
                0,
                TestAppender.events.size()
        );

        new ConfigMapper(envProvider1)
                .read(PooledJdbcConfiguration.class);

        assertEquals(
                1,
                TestAppender.events.size()
        );
        assertEquals(
                Level.WARN.toString(),
                TestAppender.events.get(0).getLevel().toString()
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

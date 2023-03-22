package fi.jubic.easyconfig.jdbc;

import fi.jubic.easyconfig.ConfigMapper;
import fi.jubic.easyconfig.providers.EnvProvider;
import fi.jubic.easyconfig.providers.StaticEnvProvider;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

public class PooledJdbcConfigurationTest {

    private static final EnvProvider envProvider1 = new StaticEnvProvider()
            .with("URL", "jdbc:h2:./target/tmp/pooled-test-1")
            .with("USER", "SA")
            .with("PASSWORD", "")
            .with("DIALECT", "H2")
            .with("POOL_SIZE", "1")
            .with("CONNECTION_TIMEOUT_MS", "1000");

    @Test
    void failedQueriesDoNotDrainConnectionPool() throws SQLException {
        PooledJdbcConfiguration configuration = new ConfigMapper(envProvider1)
                .read(PooledJdbcConfiguration.class);

        try {
            configuration.withConnection(
                    (JdbcConfiguration.ConnectionFunction<Integer>) connection -> {
                        throw new SQLException("Test");
                    }
            );
        }
        catch (SQLException ignored) {
        }

        configuration.withConnection(
                (JdbcConfiguration.ConnectionFunction<Integer>) connection -> 1
        );
    }
}

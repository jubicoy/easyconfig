package fi.jubic.easyconfig.liquibase.test;

import fi.jubic.easyconfig.ConfigMapper;
import fi.jubic.easyconfig.annotations.ConfigProperty;
import fi.jubic.easyconfig.extensions.LiquibaseExtension;
import fi.jubic.easyconfig.jdbc.PooledJdbcConfiguration;
import fi.jubic.easyconfig.providers.EnvProvider;
import fi.jubic.easyconfig.providers.StaticEnvProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class LiquibaseExtensionTest {
    private Connection connection;

    @BeforeEach
    void setup() throws SQLException {
        connection = DriverManager.getConnection(
                "jdbc:h2:file:./target/liquibase/liquibase-test-db",
                "SA",
                ""
        );

        try {
            connection.createStatement()
                    .execute("DROP TABLE DATABASECHANGELOG");
        }
        catch (SQLException ignore) {
            // Ignored
        }
        try {
            connection.createStatement()
                    .execute("DROP TABLE DATABASECHANGELOGLOCK");
        }
        catch (SQLException ignore) {
            // Ignored
        }
        try {
            connection.createStatement()
                    .execute("DROP TABLE users");
        }
        catch (SQLException ignore) {
            // Ignored
        }
    }

    @Test
    void shouldRunMigrationsByDefault() throws SQLException {
        EnvProvider envProvider = new StaticEnvProvider()
                .with("URL", "jdbc:h2:file:./target/liquibase/liquibase-test-db")
                .with("USER", "SA")
                .with("PASSWORD", "");

        ResultSet preResults = connection.createStatement()
                .executeQuery("SHOW COLUMNS FROM users;");
        assertFalse(preResults.next());

        new ConfigMapper(envProvider).read(TestConfig.class);

        ResultSet results = connection.createStatement()
                .executeQuery("SHOW COLUMNS FROM users;");

        results.first();
        assertEquals("ID", results.getString("field"));

        results.last();
        assertEquals(2, results.getRow());
        assertEquals("NAME", results.getString("field"));
    }

    @Test
    void shouldSkipMigrationWhenRunIsFalse() throws SQLException {
        EnvProvider envProvider = new StaticEnvProvider()
                .with("URL", "jdbc:h2:file:./target/liquibase/liquibase-test-db")
                .with("USER", "SA")
                .with("PASSWORD", "")
                .with("LIQUIBASE_RUN", "false");

        ResultSet preResults = connection.createStatement()
                .executeQuery("SHOW COLUMNS FROM users;");
        assertFalse(preResults.next());

        new ConfigMapper(envProvider).read(TestConfig.class);

        ResultSet preResults2 = connection.createStatement()
                .executeQuery("SHOW COLUMNS FROM users;");
        assertFalse(preResults2.next());
    }

    public static class TestConfig {
        public TestConfig(
                @LiquibaseExtension(migrations = "migrations.xml")
                @ConfigProperty("") PooledJdbcConfiguration jdbcConfiguration
        ) {

        }
    }
}

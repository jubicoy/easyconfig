package fi.jubic.easyconfig.dbunit.test;

import fi.jubic.easyconfig.ConfigMapper;
import fi.jubic.easyconfig.annotations.ConfigProperty;
import fi.jubic.easyconfig.extensions.DbUnitExtension;
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

public class DbUnitExtensionTest {
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
                    .execute("DROP TABLE user");
            connection.createStatement()
                    .execute("DROP TABLE message");
        }
        catch (SQLException ignore) {
            // Ignored
        }

        connection.createStatement()
                .execute("CREATE TABLE user (id IDENTITY PRIMARY KEY, name VARCHAR(255))");
        connection.createStatement()
                .execute(
                        "CREATE TABLE message"
                                + "(id IDENTITY PRIMARY KEY, user_id BIGINT, text VARCHAR(255))"
                );
        connection.createStatement()
                .execute("ALTER TABLE message "
                        + "ADD FOREIGN KEY (user_id) REFERENCES user(id)"
                );
    }

    @Test
    void shouldSkipPopulationByDefault() throws SQLException {
        EnvProvider envProvider = new StaticEnvProvider()
                .with("URL", "jdbc:h2:file:./target/liquibase/liquibase-test-db")
                .with("USER", "SA")
                .with("PASSWORD", "");

        new ConfigMapper(envProvider).read(TestConfig.class);

        ResultSet countResultSet = connection.createStatement()
                .executeQuery("SELECT COUNT(*) AS count FROM user");
        countResultSet.next();
        assertEquals(0, countResultSet.getInt("COUNT"));

        ResultSet messageCountResultSet = connection.createStatement()
                .executeQuery("SELECT COUNT(*) AS count FROM message");
        messageCountResultSet.next();
        assertEquals(0, messageCountResultSet.getInt("COUNT"));
    }

    @Test
    void shouldPopulateDbWhenRunIsTrue() throws SQLException {
        EnvProvider envProvider = new StaticEnvProvider()
                .with("URL", "jdbc:h2:file:./target/liquibase/liquibase-test-db")
                .with("USER", "SA")
                .with("PASSWORD", "")
                .with("DBUNIT_RUN", "true");

        new ConfigMapper(envProvider).read(TestConfig.class);

        ResultSet user1Results = connection.createStatement()
                .executeQuery("SELECT * FROM user WHERE id=1");
        user1Results.next();
        assertEquals(1, user1Results.getInt("ID"));
        assertEquals("User 1", user1Results.getString("NAME"));
        assertFalse(user1Results.next());

        ResultSet user2Results = connection.createStatement()
                .executeQuery("SELECT * FROM user WHERE id=2");
        user2Results.next();
        assertEquals(2, user2Results.getInt("ID"));
        assertEquals("User 2", user2Results.getString("NAME"));
        assertFalse(user2Results.next());

        ResultSet countResultSet = connection.createStatement()
                .executeQuery("SELECT COUNT(*) AS count FROM user");
        countResultSet.next();
        assertEquals(2, countResultSet.getInt("COUNT"));

        ResultSet message1ResultSet = connection.createStatement()
                .executeQuery("SELECT * FROM message WHERE id=1");
        message1ResultSet.next();
        assertEquals(1, message1ResultSet.getInt("ID"));
        assertEquals(1, message1ResultSet.getInt("USER_ID"));
        assertEquals("Hello there", message1ResultSet.getString("TEXT"));

        ResultSet message2ResultSet = connection.createStatement()
                .executeQuery("SELECT * FROM message WHERE id=2");
        message2ResultSet.next();
        assertEquals(2, message2ResultSet.getInt("ID"));
        assertEquals(1, message2ResultSet.getInt("USER_ID"));
        assertEquals("Hello there again", message2ResultSet.getString("TEXT"));

        ResultSet messageCountResultSet = connection.createStatement()
                .executeQuery("SELECT COUNT(*) AS count FROM message");
        messageCountResultSet.next();
        assertEquals(2, messageCountResultSet.getInt("COUNT"));
    }

    public static class TestConfig {
        public TestConfig(
                @DbUnitExtension(
                        dataset = "dataset.xml",
                        dtd = "dataset.dtd"
                )
                @ConfigProperty("") PooledJdbcConfiguration jdbcConfiguration
        ) {

        }
    }
}

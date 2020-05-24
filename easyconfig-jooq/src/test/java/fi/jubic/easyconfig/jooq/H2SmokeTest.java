package fi.jubic.easyconfig.jooq;

import fi.jubic.easyconfig.ConfigMapper;
import fi.jubic.easyconfig.jdbc.JdbcConfiguration;
import fi.jubic.easyconfig.jooq.db.tables.User;
import fi.jubic.easyconfig.jooq.db.tables.records.UserRecord;
import fi.jubic.easyconfig.providers.EnvProvider;
import fi.jubic.easyconfig.providers.StaticEnvProvider;
import org.h2.jdbc.JdbcSQLException;
import org.h2.jdbc.JdbcSQLSyntaxErrorException;
import org.jooq.Configuration;
import org.jooq.impl.DSL;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class H2SmokeTest {
    private Configuration configuration;

    @BeforeEach
    void setup() throws SQLException {
        JooqConfiguration jooqConfiguration = new ConfigMapper(envProvider)
                .read(JooqConfiguration.class);
        configuration = jooqConfiguration.getConfiguration();

        jooqConfiguration.withConnection(
                (JdbcConfiguration.ConnectionConsumer)connection -> {
                    try {
                        connection.createStatement()
                                .execute("DROP TABLE USER");
                    }
                    catch (JdbcSQLException | JdbcSQLSyntaxErrorException ignored) {
                    }

                    connection.createStatement()
                            .execute("CREATE TABLE USER (ID INT, NAME VARCHAR(50));");
                }
        );
    }

    @Test
    void h2SanityCheck() {
        assertEquals(
                0,
                DSL.using(configuration)
                        .selectCount()
                        .from(User.USER)
                        .fetchOne(0, int.class)
        );
        assertEquals(
                1,
                DSL.using(configuration)
                        .insertInto(
                                User.USER,
                                User.USER.ID,
                                User.USER.NAME
                        )
                        .values(
                                1,
                                "Hessu"
                        )
                        .execute()
        );
        assertEquals(
                1,
                DSL.using(configuration)
                        .selectCount()
                        .from(User.USER)
                        .fetchOne(0, int.class)
        );

        UserRecord user = DSL.using(configuration)
                .selectFrom(User.USER)
                .fetchOne();
        assertEquals(1, user.getId());
        assertEquals("Hessu", user.getName());

        assertEquals(
                1,
                DSL.using(configuration)
                        .deleteFrom(User.USER)
                        .where(User.USER.ID.eq(1))
                        .execute()
        );
    }

    private static EnvProvider envProvider = new StaticEnvProvider()
            .with("JOOQ_URL", "jdbc:h2:./target/tmp/ecjooq-test-db")
            .with("JOOQ_USER", "SA")
            .with("JOOQ_PASSWORD", "")
            .with("JOOQ_DIALECT", "H2");
}

package fi.jubic.easyconfig.jooq;

import fi.jubic.easyconfig.ConfigMapper;
import fi.jubic.easyconfig.EnvProvider;
import fi.jubic.easyconfig.MappingException;
import fi.jubic.easyconfig.jooq.db.tables.User;
import fi.jubic.easyconfig.jooq.db.tables.records.UserRecord;
import org.h2.jdbc.JdbcSQLException;
import org.jooq.Configuration;
import org.jooq.impl.DSL;
import org.junit.Before;
import org.junit.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.is;

public class H2SmokeTest {
    Configuration configuration;

    @Before
    public void setup() throws MappingException, SQLException {
        configuration = new ConfigMapper(envProvider)
                .read(JooqConfiguration.class)
                .getConnectionProvider();

        Connection connection = configuration.connectionProvider().acquire();

        try {
            connection.createStatement()
                    .execute("DROP TABLE USER");
        } catch (JdbcSQLException ignore) {
        }
        try {
            connection.createStatement()
                    .execute("CREATE TABLE USER (ID INT, NAME VARCHAR(50));");
        } catch (JdbcSQLException ignore) {
        }

        configuration.connectionProvider().release(connection);
    }

    @Test
    public void h2SanityCheck() {
        assertThat(
                DSL.using(configuration)
                        .selectCount()
                        .from(User.USER)
                        .fetchOne(0, int.class),
                is(0)
        );
        assertThat(
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
                        .execute(),
                is(1)
        );
        assertThat(
                DSL.using(configuration)
                        .selectCount()
                        .from(User.USER)
                        .fetchOne(0, int.class),
                is(1)
        );

        UserRecord user = DSL.using(configuration)
                .selectFrom(User.USER)
                .fetchOne();
        assertThat(user.getId(), is(1));
        assertThat(user.getName(), is("Hessu"));

        assertThat(
                DSL.using(configuration)
                        .deleteFrom(User.USER)
                        .where(User.USER.ID.eq(1))
                        .execute(),
                is(1)
        );
    }

    EnvProvider envProvider = new EnvProvider() {
        Map<String, String> envMap = new HashMap<String, String>() {{
            put("JOOQ_URL", "jdbc:h2:/tmp/ecjooq-test-db");
            put("JOOQ_USER", "SA");
            put("JOOQ_PASSWORD", "");
            put("JOOQ_DIALECT", "H2");
        }};

        @Override
        public Optional<String> getVariable(String name) {
            if (!envMap.containsKey(name)) {
                return Optional.empty();
            }
            return Optional.of(envMap.get(name));
        }
    };
}

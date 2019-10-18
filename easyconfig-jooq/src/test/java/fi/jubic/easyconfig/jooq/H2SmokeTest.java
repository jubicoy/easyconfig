package fi.jubic.easyconfig.jooq;

import fi.jubic.easyconfig.ConfigMapper;
import fi.jubic.easyconfig.EnvProvider;
import fi.jubic.easyconfig.MappingException;
import fi.jubic.easyconfig.StaticEnvProvider;
import fi.jubic.easyconfig.jooq.db.tables.User;
import fi.jubic.easyconfig.jooq.db.tables.records.UserRecord;
import org.h2.jdbc.JdbcSQLException;
import org.h2.jdbc.JdbcSQLSyntaxErrorException;
import org.jooq.Configuration;
import org.jooq.impl.DSL;
import org.junit.Before;
import org.junit.Test;

import java.sql.SQLException;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class H2SmokeTest {
    private Configuration configuration;

    @Before
    public void setup() throws MappingException, SQLException {
        JooqConfiguration jooqConfiguration = new ConfigMapper(envProvider)
                .read(JooqConfiguration.class);
        configuration = jooqConfiguration.getConfiguration();

        jooqConfiguration.withConnection(
                connection -> {
                    try {
                        connection.createStatement()
                                .execute("DROP TABLE USER");
                    } catch (JdbcSQLException | JdbcSQLSyntaxErrorException ignore) {
                    }

                    connection.createStatement()
                            .execute("CREATE TABLE USER (ID INT, NAME VARCHAR(50));");

                    return null;
                }
        );
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

    private static EnvProvider envProvider = new StaticEnvProvider() {{
        put("JOOQ_URL", "jdbc:h2:/tmp/ecjooq-test-db");
        put("JOOQ_USER", "SA");
        put("JOOQ_PASSWORD", "");
        put("JOOQ_DIALECT", "H2");
    }};
}

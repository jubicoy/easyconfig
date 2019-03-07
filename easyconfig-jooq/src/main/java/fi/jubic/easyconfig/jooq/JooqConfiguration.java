package fi.jubic.easyconfig.jooq;

import fi.jubic.easyconfig.MappingException;
import fi.jubic.easyconfig.annontations.EasyConfigProperty;
import fi.jubic.easyconfig.db.SqlDatabaseConfig;
import org.jooq.Configuration;
import org.jooq.SQLDialect;
import org.jooq.impl.DefaultConfiguration;
import org.jooq.impl.DefaultConnectionProvider;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class JooqConfiguration implements SqlDatabaseConfig {
    private final Configuration configuration;

    public JooqConfiguration(
            @EasyConfigProperty("JOOQ_URL") String url,
            @EasyConfigProperty("JOOQ_USER") String user,
            @EasyConfigProperty("JOOQ_PASSWORD") String password,
            @EasyConfigProperty("JOOQ_") JooqSettings jooqSettings,
            @EasyConfigProperty("JOOQ_DIALECT") String dialect
    ) throws MappingException {
        try {
            this.configuration = new DefaultConfiguration()
                    .set(jooqSettings)
                    .set(SQLDialect.valueOf(dialect))
                    .set(
                            new DefaultConnectionProvider(
                                    DriverManager.getConnection(url, user, password)
                            )
                    );
        } catch (SQLException e) {
            throw new MappingException(e);
        }
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    @Override
    public void withConnection(ConnectionConsumer connectionConsumer) throws SQLException {
        Configuration config = getConfiguration();
        Connection connection = config.connectionProvider().acquire();
        connectionConsumer.accept(connection);
        config.connectionProvider().release(connection);
    }

    @Override
    public <T> T withConnection(ConnectionFunction<T> connectionFunction) throws SQLException {
        Configuration config = getConfiguration();
        Connection connection = config.connectionProvider().acquire();
        T result = connectionFunction.apply(connection);
        config.connectionProvider().release(connection);
        return result;
    }
}

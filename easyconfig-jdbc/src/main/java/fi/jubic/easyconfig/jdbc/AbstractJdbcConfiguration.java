package fi.jubic.easyconfig.jdbc;

import javax.sql.DataSource;
import java.sql.SQLException;

/**
 * Simple {@link JdbcConfiguration} base class for delegating implementations.
 */
public abstract class AbstractJdbcConfiguration implements JdbcConfiguration {
    private final JdbcConfiguration configuration;

    protected AbstractJdbcConfiguration(JdbcConfiguration configuration) {
        this.configuration = configuration;
    }

    @Override
    public <T> T withConnection(ConnectionFunction<T> connectionFunction) throws SQLException {
        return configuration.withConnection(connectionFunction);
    }

    @Override
    public DataSource getDataSource() {
        return configuration.getDataSource();
    }
}

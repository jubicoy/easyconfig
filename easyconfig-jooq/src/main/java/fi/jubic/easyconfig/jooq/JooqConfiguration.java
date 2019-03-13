package fi.jubic.easyconfig.jooq;

import com.zaxxer.hikari.HikariDataSource;
import fi.jubic.easyconfig.MappingException;
import fi.jubic.easyconfig.annontations.EasyConfigProperty;
import fi.jubic.easyconfig.db.SqlDatabaseConfig;
import org.jooq.Configuration;
import org.jooq.SQLDialect;
import org.jooq.impl.DataSourceConnectionProvider;
import org.jooq.impl.DefaultConfiguration;

import java.sql.Connection;
import java.sql.SQLException;

public class JooqConfiguration implements SqlDatabaseConfig {
    private final HikariDataSource dataSource;
    private final Configuration configuration;

    public JooqConfiguration(
            @EasyConfigProperty("JOOQ_URL") String url,
            @EasyConfigProperty("JOOQ_USER") String user,
            @EasyConfigProperty("JOOQ_PASSWORD") String password,
            @EasyConfigProperty("JOOQ_") JooqSettings jooqSettings,
            @EasyConfigProperty("JOOQ_DIALECT") String dialect
    ) throws MappingException {
        this.dataSource = new HikariDataSource();
        this.dataSource.setJdbcUrl(url);
        this.dataSource.setUsername(user);
        this.dataSource.setPassword(password);

        this.configuration = new DefaultConfiguration()
                .set(jooqSettings)
                .set(SQLDialect.valueOf(dialect))
                .set(new DataSourceConnectionProvider(this.dataSource));
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    @Override
    public void withConnection(ConnectionConsumer connectionConsumer) throws SQLException {
        Connection connection = dataSource.getConnection();
        connectionConsumer.accept(connection);
        connection.close();
    }

    @Override
    public <T> T withConnection(ConnectionFunction<T> connectionFunction) throws SQLException {
        Connection connection = dataSource.getConnection();
        T result = connectionFunction.apply(connection);
        connection.close();
        return result;
    }
}

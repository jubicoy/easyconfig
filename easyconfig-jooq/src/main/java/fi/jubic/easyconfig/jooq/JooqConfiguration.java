package fi.jubic.easyconfig.jooq;

import com.zaxxer.hikari.HikariDataSource;
import fi.jubic.easyconfig.annotations.EasyConfigProperty;
import fi.jubic.easyconfig.db.SqlDatabaseConfig;
import org.jooq.Configuration;
import org.jooq.SQLDialect;
import org.jooq.impl.DataSourceConnectionProvider;
import org.jooq.impl.DefaultConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class JooqConfiguration implements SqlDatabaseConfig {
    private static final Logger logger = LoggerFactory.getLogger(JooqConfiguration.class);
    private static final Set<ConnectionFingerprint> globalConnections =
            ConcurrentHashMap.newKeySet();

    private final HikariDataSource dataSource;
    private final Configuration configuration;

    /**
     * Constructor used for injection.
     */
    public JooqConfiguration(
            @EasyConfigProperty("JOOQ_URL") String url,
            @EasyConfigProperty("JOOQ_USER") String user,
            @EasyConfigProperty("JOOQ_PASSWORD") String password,
            @EasyConfigProperty("JOOQ_") JooqSettings jooqSettings,
            @EasyConfigProperty("JOOQ_DIALECT") String dialect,
            @EasyConfigProperty(
                    value = "JOOQ_DRIVER_CLASS_NAME",
                    defaultValue = ""
            ) String driverClassName,
            @EasyConfigProperty(
                    value = "JOOQ_POOL_SIZE",
                    defaultValue = "-1"
            ) int poolSize
    ) {
        ConnectionFingerprint fingerprint = new ConnectionFingerprint(
                url,
                user,
                password
        );
        if (globalConnections.contains(fingerprint)) {
            logger.warn(
                    "Multiple connection pools initialized with the same connection "
                            + "parameters to {}. Make sure a singleton configuration "
                            + "is used.",
                    url
            );
        }
        globalConnections.add(fingerprint);

        this.dataSource = new HikariDataSource();
        this.dataSource.setJdbcUrl(url);
        this.dataSource.setUsername(user);
        this.dataSource.setPassword(password);

        if (!driverClassName.isEmpty()) {
            this.dataSource.setDriverClassName(driverClassName);
        }

        if (poolSize > 0) {
            this.dataSource.setMaximumPoolSize(poolSize);
        }

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

    private static final class ConnectionFingerprint {
        private final String value;

        ConnectionFingerprint(
                String url,
                String user,
                String password
        ) {
            this.value = url + user + password;
        }

        @Override
        public int hashCode() {
            return this.value.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) return false;
            if (!(obj instanceof ConnectionFingerprint)) return false;

            ConnectionFingerprint castObj = (ConnectionFingerprint) obj;

            return Objects.equals(value, castObj.value);
        }
    }
}

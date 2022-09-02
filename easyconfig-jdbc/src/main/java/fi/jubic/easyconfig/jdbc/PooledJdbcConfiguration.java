package fi.jubic.easyconfig.jdbc;

import com.zaxxer.hikari.HikariDataSource;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import fi.jubic.easyconfig.annotations.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A pooled JDBC configuration.
 */
@SuppressFBWarnings(
        value = "EI_EXPOSE_REP",
        justification = "This is the intended behavior for now."
)
public class PooledJdbcConfiguration implements JdbcConfiguration {
    private static final Logger logger = LoggerFactory.getLogger(PooledJdbcConfiguration.class);
    private static final Set<ConnectionFingerprint> globalConnections =
            ConcurrentHashMap.newKeySet();

    private final HikariDataSource dataSource;

    /**
     * Constructor used for injection.
     */
    @SuppressWarnings("WeakerAccess")
    public PooledJdbcConfiguration(
            @ConfigProperty("URL") String url,
            @ConfigProperty("USER") String user,
            @ConfigProperty("PASSWORD") String password,
            @ConfigProperty(value = "DRIVER_CLASS_NAME", defaultValue = "") String driverClassName,
            @ConfigProperty(value = "POOL_SIZE", defaultValue = "-1") int poolSize,
            @ConfigProperty(
                    value = "CONNECTION_TIMEOUT_MS",
                    defaultValue = "0"
            ) int connectionTimeout
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

        if (connectionTimeout > 0) {
            this.dataSource.setConnectionTimeout(connectionTimeout);
        }
    }

    @Override
    public <T> T withConnection(ConnectionFunction<T> connectionFunction) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            return connectionFunction.apply(connection);
        }
    }

    @Override
    public DataSource getDataSource() {
        return this.dataSource;
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

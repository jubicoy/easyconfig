package fi.jubic.easyconfig.jdbc;

import fi.jubic.easyconfig.db.SqlDatabaseConfig;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Generic interface for a configuration that provides possibly releasable instances of
 * {@link java.sql.Connection}.
 *
 * <p>
 *     This interface facilitates utility services that require access to the database without the
 *     data access interface - for example, running database migrations.
 * </p>
 */
@SuppressWarnings("deprecation")
public interface JdbcConfiguration extends SqlDatabaseConfig {
    /**
     * Calls the given consumer with a connection acquired from this configuration.
     *
     * @param connectionConsumer the consumer to be called
     * @throws SQLException exception thrown by the configuration or the {@code ConnectionConsumer}
     */
    default void withConnection(ConnectionConsumer connectionConsumer) throws SQLException {
        this.withConnection((ConnectionFunction<Void>) connection -> {
            connectionConsumer.accept(connection);
            return null;
        });
    }

    /**
     * Applies a connection acquired from this configuration to the given function. Either the
     * configuration or the function can throw an {@code SQLException}.
     *
     * @param connectionFunction the function to be called
     * @param <T> the type of the return value
     * @return the value returned by the given function
     * @throws SQLException exception thrown by the configuration or the {@code ConnectionFunction}
     */
    <T> T withConnection(ConnectionFunction<T> connectionFunction) throws SQLException;

    /**
     * A hack to support external dependencies on {@link SqlDatabaseConfig}.
     */
    @Deprecated
    @Override
    default void withConnection(
            SqlDatabaseConfig.ConnectionConsumer connectionConsumer
    ) throws SQLException {
        withConnection((ConnectionConsumer) connectionConsumer::accept);
    }

    /**
     * A hack to support external dependencies on {@link SqlDatabaseConfig}.
     */
    @Deprecated
    @Override
    default <T> T withConnection(
            SqlDatabaseConfig.ConnectionFunction<T> connectionFunction
    ) throws SQLException {
        return withConnection((ConnectionFunction<T>) connectionFunction::apply);
    }

    /**
     * Return the {@code DataSource} managed by this configuration.
     * @return a managed data source
     */
    @Deprecated
    DataSource getDataSource();

    /**
     * {@link java.util.function.Consumer} that can throw a {@link SQLException}.
     */
    @FunctionalInterface
    interface ConnectionConsumer {
        void accept(Connection connection) throws SQLException;
    }

    /**
     * {@link java.util.function.Function} that can throw a {@code SQLException}.
     * @param <T> the type of the return value
     */
    interface ConnectionFunction<T> {
        T apply(Connection connection) throws SQLException;
    }
}

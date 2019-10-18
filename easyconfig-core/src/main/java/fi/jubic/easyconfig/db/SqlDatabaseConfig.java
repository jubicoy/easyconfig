package fi.jubic.easyconfig.db;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Generic interface for a configuration that provides possibly
 * releasable instances of {@link java.sql.Connection}.
 *
 * This interface facilitates utility services that require access
 * to the database without the data access interface - for example,
 * running database migrations.
 */
public interface SqlDatabaseConfig {
    void withConnection(ConnectionConsumer connectionConsumer) throws SQLException;
    <T> T withConnection(ConnectionFunction<T> connectionFunction) throws SQLException;

    interface ConnectionConsumer {
        void accept(Connection connection) throws SQLException;
    }

    interface ConnectionFunction<T> {
        T apply(Connection connection) throws SQLException;
    }
}

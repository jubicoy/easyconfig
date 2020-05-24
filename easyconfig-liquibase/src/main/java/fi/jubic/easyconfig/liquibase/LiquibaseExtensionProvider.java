package fi.jubic.easyconfig.liquibase;

import fi.jubic.easyconfig.annotations.ConfigProperty;
import fi.jubic.easyconfig.extensions.ConfigExtensionProvider;
import fi.jubic.easyconfig.extensions.LiquibaseExtension;
import fi.jubic.easyconfig.jdbc.JdbcConfiguration;
import liquibase.Contexts;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;

import java.sql.SQLException;
import java.util.Optional;

public class LiquibaseExtensionProvider
        implements ConfigExtensionProvider<LiquibaseExtension, JdbcConfiguration> {
    private final boolean runOnIntialize;

    public LiquibaseExtensionProvider(
            @ConfigProperty(
                    value = "LIQUIBASE_RUN",
                    defaultValue = "true"
            ) boolean runOnInitialize
    ) {
        this.runOnIntialize = runOnInitialize;
    }

    @Override
    public JdbcConfiguration extend(
            LiquibaseExtension extensionParams,
            JdbcConfiguration configuration
    ) {
        if (!runOnIntialize) {
            return configuration;
        }

        String migrationsFile = Optional.ofNullable(extensionParams)
                .orElse(LiquibaseExtension.DEFAULT)
                .migrations();

        try {
            configuration.withConnection(
                    (JdbcConfiguration.ConnectionConsumer) connection -> {
                        try {
                            Database database = DatabaseFactory.getInstance()
                                    .findCorrectDatabaseImplementation(
                                            new JdbcConnection(connection)
                                    );

                            Liquibase liquibase = new Liquibase(
                                    migrationsFile,
                                    new ClassLoaderResourceAccessor(),
                                    database
                            );

                            liquibase.update(new Contexts());
                        }
                        catch (LiquibaseException exception) {
                            throw new IllegalStateException("Liquibase update failed", exception);
                        }
                    }
            );
        }
        catch (SQLException exception) {
            throw new IllegalStateException("Liquibase update failed", exception);
        }

        return configuration;
    }
}

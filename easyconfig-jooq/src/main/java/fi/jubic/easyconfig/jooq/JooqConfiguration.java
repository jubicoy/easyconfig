package fi.jubic.easyconfig.jooq;

import fi.jubic.easyconfig.annotations.ConfigProperty;
import fi.jubic.easyconfig.jdbc.AbstractJdbcConfiguration;
import fi.jubic.easyconfig.jdbc.PooledJdbcConfiguration;
import org.jooq.Configuration;
import org.jooq.SQLDialect;
import org.jooq.impl.DataSourceConnectionProvider;
import org.jooq.impl.DefaultConfiguration;

/**
 * JOOQ {@link Configuration} initializer class implemented on top of a
 * {@link PooledJdbcConfiguration}.
 */
public class JooqConfiguration extends AbstractJdbcConfiguration {
    private final Configuration configuration;

    /**
     * Constructor used for injection.
     */
    public JooqConfiguration(
            @ConfigProperty("JOOQ_") PooledJdbcConfiguration jdbcConfiguration,
            @ConfigProperty("JOOQ_") JooqSettings jooqSettings,
            @ConfigProperty("JOOQ_DIALECT") String dialect
    ) {
        super(jdbcConfiguration);
        this.configuration = new DefaultConfiguration()
                .set(jooqSettings)
                .set(SQLDialect.valueOf(dialect))
                .set(new DataSourceConnectionProvider(getDataSource()));
    }

    public Configuration getConfiguration() {
        return configuration;
    }
}

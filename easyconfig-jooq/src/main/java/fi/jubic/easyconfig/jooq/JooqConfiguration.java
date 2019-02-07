package fi.jubic.easyconfig.jooq;

import fi.jubic.easyconfig.annontations.EasyConfigProperty;
import org.jooq.Configuration;
import org.jooq.SQLDialect;
import org.jooq.impl.DefaultConfiguration;
import org.jooq.impl.DefaultConnectionProvider;

import java.sql.DriverManager;
import java.sql.SQLException;

public class JooqConfiguration {
    private String url;
    private String user;
    private String password;

    private SQLDialect dialect;
    private JooqSettings settings;

    private Configuration configuration = null;

    public JooqConfiguration(
            @EasyConfigProperty("JOOQ_URL") String url,
            @EasyConfigProperty("JOOQ_USER") String user,
            @EasyConfigProperty("JOOQ_PASSWORD") String password,
            @EasyConfigProperty("JOOQ_") JooqSettings jooqSettings,
            @EasyConfigProperty("JOOQ_DIALECT") String dialect
    ) {
        this.url = url;
        this.user = user;
        this.password = password;
        this.settings = jooqSettings;
        this.dialect = SQLDialect.valueOf(dialect);
    }

    public Configuration getConnectionProvider() throws SQLException {
        if (configuration == null) {
            configuration = new DefaultConfiguration()
                    .set(settings)
                    .set(dialect)
                    .set(
                            new DefaultConnectionProvider(
                                    DriverManager.getConnection(url, user, password)
                            )
                    );
        }
        return configuration;
    }
}

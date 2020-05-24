package fi.jubic.easyconfig.dbunit;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import fi.jubic.easyconfig.annotations.ConfigProperty;
import fi.jubic.easyconfig.dbunit.template.Base64Encoder;
import fi.jubic.easyconfig.dbunit.template.DateObject;
import fi.jubic.easyconfig.extensions.ConfigExtensionProvider;
import fi.jubic.easyconfig.extensions.DbUnitExtension;
import fi.jubic.easyconfig.jdbc.JdbcConfiguration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.dbunit.DatabaseUnitException;
import org.dbunit.database.DatabaseConfig;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.datatype.DefaultDataTypeFactory;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.ext.h2.H2DataTypeFactory;
import org.dbunit.ext.hsqldb.HsqldbDataTypeFactory;
import org.dbunit.ext.mysql.MySqlDataTypeFactory;
import org.dbunit.ext.postgresql.PostgresqlDataTypeFactory;
import org.dbunit.operation.DatabaseOperation;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@SuppressFBWarnings(
        value = "TEMPLATE_INJECTION_FREEMARKER",
        justification = "The templates are provided by the developers, not users."
)
public class DbUnitExtensionProvider
        implements ConfigExtensionProvider<DbUnitExtension, JdbcConfiguration> {
    private final boolean runOnInitialize;

    public DbUnitExtensionProvider(
            @ConfigProperty(
                    value = "DBUNIT_RUN",
                    defaultValue = "false"
            ) boolean runOnInitialize
    ) {
        this.runOnInitialize = runOnInitialize;
    }

    @Override
    public JdbcConfiguration extend(
            DbUnitExtension extensionParams,
            JdbcConfiguration configuration
    ) {
        if (!runOnInitialize) {
            return configuration;
        }

        ClassLoader classLoader = configuration.getClass().getClassLoader();

        try {
            configuration.withConnection(
                    (JdbcConfiguration.ConnectionConsumer) connection -> {
                        try {
                            DatabaseConnection databaseConnection = new DatabaseConnection(
                                    connection
                            );

                            Driver driver = DriverManager.getDriver(
                                    connection.getMetaData()
                                            .getURL()
                            );
                            String driverName = driver.getClass().getName();

                            DefaultDataTypeFactory dataTypeFactory;
                            switch (driverName) {
                                case "org.postgresql.Driver":
                                    dataTypeFactory = new PostgresqlDataTypeFactory();
                                    break;

                                case "com.mysql.jdbc.Driver":
                                case "com.mysql.cj.jdbc.Driver":
                                    dataTypeFactory = new MySqlDataTypeFactory();
                                    break;

                                case "org.hsqldb.jdbcDriver":
                                    dataTypeFactory = new HsqldbDataTypeFactory();
                                    break;

                                case "org.h2.Driver":
                                    dataTypeFactory = new H2DataTypeFactory();
                                    break;

                                default:
                                    dataTypeFactory = new DefaultDataTypeFactory();
                                    break;
                            }

                            DatabaseConfig databaseConfig = databaseConnection.getConfig();
                            databaseConfig.setProperty(
                                    DatabaseConfig.PROPERTY_DATATYPE_FACTORY,
                                    dataTypeFactory
                            );

                            InputStream dtdStream = classLoader.getResourceAsStream(
                                    extensionParams.dtd()
                            );
                            InputStream dataSetStream = classLoader.getResourceAsStream(
                                    extensionParams.dataset()
                            );
                            IDataSet dataSet = new FlatXmlDataSetBuilder()
                                    .setMetaDataSetFromDtd(dtdStream)
                                    .build(processStream(dataSetStream));

                            DatabaseOperation.CLEAN_INSERT.execute(databaseConnection, dataSet);

                            try (
                                    PreparedStatement statement
                                            = connection.prepareStatement("commit;")
                            ) {
                                statement.execute();
                            }

                            Objects.requireNonNull(dtdStream).close();
                            Objects.requireNonNull(dataSetStream).close();
                        }
                        catch (DatabaseUnitException
                                | IOException
                                | TemplateException exception
                        ) {
                            throw new IllegalStateException(exception);
                        }
                    }
            );
        }
        catch (SQLException exception) {
            throw new IllegalStateException(exception);
        }

        return configuration;
    }

    private InputStream processStream(
            InputStream is
    ) throws IOException, TemplateException {
        Template t = new Template(
                "dataset",
                new InputStreamReader(is, StandardCharsets.UTF_8),
                new freemarker.template.Configuration(
                        freemarker.template.Configuration.VERSION_2_3_23
                )
        );

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Map<String, Object> model = new HashMap<>();
        model.put("t", new DateObject(new Date()));
        model.put("base64", new Base64Encoder());
        t.process(model, new OutputStreamWriter(out, StandardCharsets.UTF_8));
        return new ByteArrayInputStream(out.toByteArray());
    }
}

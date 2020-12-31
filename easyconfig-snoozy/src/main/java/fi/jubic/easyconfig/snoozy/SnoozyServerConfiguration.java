package fi.jubic.easyconfig.snoozy;

import fi.jubic.easyconfig.annotations.ConfigProperty;
import fi.jubic.snoozy.MultipartConfig;
import fi.jubic.snoozy.ServerConfiguration;
import fi.jubic.snoozy.swagger.SwaggerConfig;

import java.util.Objects;

public class SnoozyServerConfiguration implements ServerConfiguration {
    private final String hostname;
    private final int port;
    private final boolean devMode;
    private final SnoozyMultipartConfig multipartConfig;
    private final SnoozySwaggerConfig swaggerConfig;

    /**
     * Constructor used for injection.
     */
    public SnoozyServerConfiguration(
            @ConfigProperty(value = "HOST", defaultValue = "localhost") String hostname,
            @ConfigProperty(value = "PORT", defaultValue = "8080") int port,
            @ConfigProperty(
                    value = "DEPLOYMENT_ENVIRONMENT",
                    defaultValue = "production",
                    noPrefix = true
            ) String environment,
            @ConfigProperty("MULTIPART_") SnoozyMultipartConfig multipartConfig,
            @ConfigProperty("SWAGGER_") SnoozySwaggerConfig swaggerConfig
    ) {
        this.hostname = hostname;
        this.port = port;
        this.devMode = Objects.equals(environment, "development");
        this.multipartConfig = multipartConfig;
        this.swaggerConfig = swaggerConfig;
    }

    @Override
    public String getHostname() {
        return hostname;
    }

    @Override
    public int getPort() {
        return port;
    }

    @Override
    public boolean isDevMode() {
        return devMode;
    }

    @Override
    public MultipartConfig getMultipartConfig() {
        return multipartConfig;
    }

    @Override
    public SwaggerConfig getSwaggerConfig() {
        return swaggerConfig;
    }
}

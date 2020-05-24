package fi.jubic.easyconfig.snoozy;

import fi.jubic.easyconfig.annotations.ConfigProperty;
import fi.jubic.snoozy.MultipartConfig;
import fi.jubic.snoozy.ServerConfiguration;

@SuppressWarnings("WeakerAccess")
public class SnoozyServerConfiguration implements ServerConfiguration {
    private final String hostname;
    private final int port;
    private final SnoozyMultipartConfig multipartConfig;

    /**
     * Constructor used for injection.
     */
    public SnoozyServerConfiguration(
            @ConfigProperty(value = "HOST", defaultValue = "localhost") String hostname,
            @ConfigProperty(value = "PORT", defaultValue = "8080") int port,
            @ConfigProperty(value = "MULTIPART_") SnoozyMultipartConfig multipartConfig
    ) {
        this.hostname = hostname;
        this.port = port;
        this.multipartConfig = multipartConfig;
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
    public MultipartConfig getMultipartConfig() {
        return multipartConfig;
    }
}

package fi.jubic.easyconfig.snoozy;

import fi.jubic.easyconfig.annotations.EasyConfigProperty;
import fi.jubic.snoozy.MultipartConfig;
import fi.jubic.snoozy.ServerConfiguration;

public class SnoozyServerConfiguration implements ServerConfiguration {
    private final String hostname;
    private final int port;
    private final SnoozyMultipartConfig multipartConfig;

    public SnoozyServerConfiguration(
            @EasyConfigProperty(value = "HOST", defaultValue = "localhost") String hostname,
            @EasyConfigProperty(value = "PORT", defaultValue = "8080") int port,
            @EasyConfigProperty(value = "MULTIPART_") SnoozyMultipartConfig multipartConfig
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

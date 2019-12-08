package fi.jubic.easyconfig.snoozy;

import fi.jubic.easyconfig.ConfigMapper;
import fi.jubic.easyconfig.EnvProvider;
import fi.jubic.easyconfig.MappingException;
import fi.jubic.easyconfig.StaticEnvProvider;
import fi.jubic.snoozy.MultipartConfig;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MultipartDefaultsTest {
    @Test
    void testProvidedValuesPreferred() throws MappingException {
        EnvProvider envProvider = new StaticEnvProvider()
                .with("CACHE_LOCATION", "/tmp/snoozy")
                .with("MAX_FILE_SIZE", "1024")
                .with("MAX_REQUEST_SIZE", "2048")
                .with("SIZE_THRESHOLD", "512");

        MultipartConfig multipartConfig = new ConfigMapper(envProvider)
                .read(SnoozyMultipartConfig.class);

        assertEquals("/tmp/snoozy", multipartConfig.getCacheLocation());
        assertEquals(1024, multipartConfig.getMaxFileSize());
        assertEquals(2048, multipartConfig.getMaxRequestSize());
        assertEquals(512, multipartConfig.getSizeThreshold());
    }

    @Test
    void testFallbackToDefaults() throws MappingException {
        EnvProvider envProvider = new StaticEnvProvider();

        MultipartConfig multipartConfig = new ConfigMapper(envProvider)
                .read(SnoozyMultipartConfig.class);

        MultipartConfig defaultConfig = new MultipartConfig() {};

        assertEquals(defaultConfig.getCacheLocation(), multipartConfig.getCacheLocation());
        assertEquals(defaultConfig.getMaxFileSize(), multipartConfig.getMaxFileSize());
        assertEquals(defaultConfig.getMaxRequestSize(), multipartConfig.getMaxRequestSize());
        assertEquals(defaultConfig.getSizeThreshold(), multipartConfig.getSizeThreshold());
    }
}

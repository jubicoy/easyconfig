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
        EnvProvider envProvider = new StaticEnvProvider() {{
            put("CACHE_LOCATION", "/tmp/snoozy");
            put("MAX_FILE_SIZE", "1024");
            put("MAX_REQUEST_SIZE", "2048");
            put("SIZE_THRESHOLD", "512");
        }};

        MultipartConfig multipartConfig = new ConfigMapper(envProvider).read(SnoozyMultipartConfig.class);

        assertEquals("/tmp/snoozy", multipartConfig.getCacheLocation());
        assertEquals(1024, multipartConfig.getMaxFileSize());
        assertEquals(2048, multipartConfig.getMaxRequestSize());
        assertEquals(512, multipartConfig.getSizeThreshold());
    }

    @Test
    void testFallbackToDefaults() throws MappingException {
        EnvProvider envProvider = new StaticEnvProvider();

        MultipartConfig multipartConfig = new ConfigMapper(envProvider).read(SnoozyMultipartConfig.class);

        MultipartConfig defaultConfig = new MultipartConfig() {};

        assertEquals(defaultConfig.getCacheLocation(), multipartConfig.getCacheLocation());
        assertEquals(defaultConfig.getMaxFileSize(), multipartConfig.getMaxFileSize());
        assertEquals(defaultConfig.getMaxRequestSize(), multipartConfig.getMaxRequestSize());
        assertEquals(defaultConfig.getSizeThreshold(), multipartConfig.getSizeThreshold());
    }
}

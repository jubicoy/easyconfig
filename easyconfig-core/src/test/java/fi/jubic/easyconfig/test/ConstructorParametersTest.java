package fi.jubic.easyconfig.test;

import fi.jubic.easyconfig.ConfigMapper;
import fi.jubic.easyconfig.annotations.ConfigProperty;
import fi.jubic.easyconfig.annotations.EnvProviderProperty;
import fi.jubic.easyconfig.providers.EnvProvider;
import fi.jubic.easyconfig.providers.StaticEnvProvider;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ConstructorParametersTest {
    @Test
    void testConstructorParametersMapping() {
        TestConfig config = new ConfigMapper(envProvider)
                .read(TestConfig.class);

        assertEquals(111L, config.id);
        assertEquals("127.1.0.1", config.host);
    }

    @Test
    void testConstructorParameterEnvProviderMapping() {
        TestConfig2 config = new ConfigMapper(envProvider)
                .read(TestConfig2.class);

        assertEquals(111L, config.id);
        assertNotNull(config.envProvider);
    }

    private static EnvProvider envProvider = new StaticEnvProvider()
            .with("ID", "111")
            .with("HOST", "127.1.0.1");

    @SuppressWarnings("WeakerAccess")
    public static class TestConfig {
        final Long id;
        final String host;

        public TestConfig(
                @ConfigProperty("ID") Long id,
                @ConfigProperty("HOST") String host
        ) {
            this.id = id;
            this.host = host;
        }
    }

    @SuppressWarnings("WeakerAccess")
    public static class TestConfig2 {
        final Long id;
        final EnvProvider envProvider;

        @SuppressWarnings("WeakerAccess")
        public TestConfig2(
                @ConfigProperty("ID") Long id,
                @EnvProviderProperty EnvProvider envProvider
        ) {
            this.id = id;
            this.envProvider = envProvider;
        }
    }
}

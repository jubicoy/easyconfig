package fi.jubic.easyconfig;

import fi.jubic.easyconfig.annotations.ConfigProperty;
import fi.jubic.easyconfig.annotations.EasyConfigProperty;
import fi.jubic.easyconfig.annotations.EnvProviderProperty;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ConstructorParametersTest {
    @Test
    void testConstructorParametersMapping() throws MappingException {
        TestConfig config = new ConfigMapper(envProvider)
                .read(TestConfig.class);

        assertEquals(111L, config.id);
        assertEquals("127.1.0.1", config.host);
    }

    @Test
    void testConstructorParameterEnvProviderMapping() throws MappingException {
        TestConfig2 config = new ConfigMapper(envProvider)
                .read(TestConfig2.class);

        assertEquals(111L, config.id);
        assertNotNull(config.envProvider);
    }

    private static EnvProvider envProvider = new StaticEnvProvider()
            .with("ID", "111")
            .with("HOST", "127.1.0.1");

    static class TestConfig {
        final Long id;
        final String host;

        public TestConfig(
                @EasyConfigProperty("ID") Long id,
                @EasyConfigProperty("HOST") String host
        ) {
            this.id = id;
            this.host = host;
        }
    }

    static class TestConfig2 {
        final Long id;
        final EnvProvider envProvider;

        public TestConfig2(
                @ConfigProperty("ID") Long id,
                @EnvProviderProperty EnvProvider envProvider
        ) {
            this.id = id;
            this.envProvider = envProvider;
        }
    }
}

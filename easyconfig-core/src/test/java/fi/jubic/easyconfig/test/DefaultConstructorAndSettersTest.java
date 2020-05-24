package fi.jubic.easyconfig.test;

import fi.jubic.easyconfig.ConfigMapper;
import fi.jubic.easyconfig.annotations.ConfigProperty;
import fi.jubic.easyconfig.annotations.EnvProviderProperty;
import fi.jubic.easyconfig.providers.EnvProvider;
import fi.jubic.easyconfig.providers.StaticEnvProvider;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class DefaultConstructorAndSettersTest {
    @Test
    void testDefaultConstructorAndSettersMapping() {
        TestConfig config = new ConfigMapper(envProvider)
                .read(TestConfig.class);

        assertEquals(111L, config.id);
        assertEquals("127.1.0.1", config.host);
    }

    @Test
    void testDefaultConstructorAndSettersEnvProviderMapping() {
        TestConfig2 config = new ConfigMapper(envProvider)
                .read(TestConfig2.class);

        assertEquals(111L, config.id);
        assertNotNull(config.envProvider);
    }

    private static EnvProvider envProvider = new StaticEnvProvider()
            .with("ID", "111")
            .with("HOST", "127.1.0.1");

    public static class TestConfig {
        Long id;
        String host;

        public TestConfig() {
        }

        @ConfigProperty("ID")
        public void setId(Long id) {
            this.id = id;
        }

        @ConfigProperty("HOST")
        public void setHost(String host) {
            this.host = host;
        }
    }

    public static class TestConfig2 {
        Long id;
        EnvProvider envProvider;

        public TestConfig2() {

        }

        @ConfigProperty("ID")
        public void setId(Long id) {
            this.id = id;
        }

        @EnvProviderProperty
        public void setEnvProvider(EnvProvider envProvider) {
            this.envProvider = envProvider;
        }
    }
}

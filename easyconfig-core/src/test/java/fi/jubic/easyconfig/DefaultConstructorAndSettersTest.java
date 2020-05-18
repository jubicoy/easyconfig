package fi.jubic.easyconfig;

import fi.jubic.easyconfig.annotations.ConfigProperty;
import fi.jubic.easyconfig.annotations.EasyConfigProperty;
import fi.jubic.easyconfig.annotations.EnvProviderProperty;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class DefaultConstructorAndSettersTest {
    @Test
    void testDefaultConstructorAndSettersMapping() throws MappingException {
        TestConfig config = new ConfigMapper(envProvider)
                .read(TestConfig.class);

        assertEquals(111L, config.id);
        assertEquals("127.1.0.1", config.host);
    }

    @Test
    void testDefaultConstructorAndSettersEnvProviderMapping() throws MappingException {
        TestConfig2 config = new ConfigMapper(envProvider)
                .read(TestConfig2.class);

        assertEquals(111L, config.id);
        assertNotNull(config.envProvider);
    }

    private static EnvProvider envProvider = new StaticEnvProvider()
            .with("ID", "111")
            .with("HOST", "127.1.0.1");

    static class TestConfig {
        Long id;
        String host;

        public TestConfig() {
        }

        @EasyConfigProperty("ID")
        public void setId(Long id) {
            this.id = id;
        }

        @EasyConfigProperty("HOST")
        public void setHost(String host) {
            this.host = host;
        }
    }

    static class TestConfig2 {
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

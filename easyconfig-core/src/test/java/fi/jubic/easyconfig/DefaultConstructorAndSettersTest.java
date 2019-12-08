package fi.jubic.easyconfig;

import fi.jubic.easyconfig.annotations.EasyConfigProperty;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DefaultConstructorAndSettersTest {
    @Test
    void testDefaultConstructorAndSettersMapping() throws MappingException {
        TestConfig config = new ConfigMapper(envProvider)
                .read(TestConfig.class);

        assertEquals(111L, config.id);
        assertEquals("127.1.0.1", config.host);
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
}

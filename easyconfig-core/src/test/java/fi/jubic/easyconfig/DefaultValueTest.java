package fi.jubic.easyconfig;

import fi.jubic.easyconfig.annotations.EasyConfigProperty;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DefaultValueTest {
    @Test
    void testConstructorParametersMapping() throws MappingException {
        TestConfig config = new ConfigMapper(envProvider)
                .read(TestConfig.class);

        assertEquals(111L, config.id);
        assertEquals("127.1.0.1", config.host);
    }

    private static EnvProvider envProvider = new StaticEnvProvider();

    static class TestConfig {
        final Long id;
        final String host;

        public TestConfig(
                @EasyConfigProperty(
                        value = "ID",
                        defaultValue = "111"
                ) Long id,
                @EasyConfigProperty(
                        value = "HOST",
                        defaultValue = "127.1.0.1"
                ) String host
        ) {
            this.id = id;
            this.host = host;
        }
    }
}

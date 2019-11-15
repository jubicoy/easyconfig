package fi.jubic.easyconfig;

import fi.jubic.easyconfig.annotations.EasyConfigProperty;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ConstructorParametersTest {
    @Test
    void testConstructorParametersMapping() throws MappingException {
        TestConfig config = new ConfigMapper(envProvider)
                .read(TestConfig.class);

        assertEquals(111L, config.id);
        assertEquals("127.1.0.1", config.host);
    }

    private static EnvProvider envProvider = new StaticEnvProvider() {{
        put("ID", "111");
        put("HOST", "127.1.0.1");
    }};

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
}

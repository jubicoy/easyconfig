package fi.jubic.easyconfig.test;

import fi.jubic.easyconfig.ConfigMapper;
import fi.jubic.easyconfig.annotations.ConfigProperty;
import fi.jubic.easyconfig.providers.EnvProvider;
import fi.jubic.easyconfig.providers.StaticEnvProvider;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DefaultValueTest {
    @Test
    void testConstructorParametersMapping() {
        TestConfig config = new ConfigMapper(envProvider)
                .read(TestConfig.class);

        assertEquals(111L, config.id);
        assertEquals("127.1.0.1", config.host);
        assertEquals(Collections.emptyList(), config.stringList);
    }

    private static EnvProvider envProvider = new StaticEnvProvider();

    public static class TestConfig {
        final Long id;
        final String host;
        final List<String> stringList;

        public TestConfig(
                @ConfigProperty(
                        value = "ID",
                        defaultValue = "111"
                ) Long id,
                @ConfigProperty(
                        value = "HOST",
                        defaultValue = "127.1.0.1"
                ) String host,
                @ConfigProperty(
                        value = "STR_LIST",
                        defaultValue = ""
                ) List<String> stringList
        ) {
            this.id = id;
            this.host = host;
            this.stringList = stringList;
        }
    }
}

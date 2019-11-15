package fi.jubic.easyconfig;

import fi.jubic.easyconfig.annotations.EasyConfigProperty;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ListSupportTest {
    @Test
    void listParametersMapping() throws MappingException {
        TestConfig config = new ConfigMapper(
                new StaticEnvProvider() {{
                    put("ID", "111");
                    put("HOSTS", "127.1.0.1;127.1.0.2");
                }}
        ).read(TestConfig.class);

        assertEquals(111L, config.id);
        assertEquals(Arrays.asList("127.1.0.1", "127.1.0.2"), config.hosts);
    }

    @Test
    void listParametersMappingThrowsWhenParameterMissing() {
        assertThrows(
                MappingException.class,
                () -> new ConfigMapper(
                        new StaticEnvProvider() {{
                            put("ID", "111");
                        }}
                ).read(TestConfig.class)
        );
    }

    @Test
    void emptyDefaultMappedToEmptyList() throws MappingException {
        DefaultTestConfig config = new ConfigMapper(
                new StaticEnvProvider() {{
                    put("ID", "111");
                }}
        ).read(DefaultTestConfig.class);

        assertEquals(111L, config.id);
        assertTrue(config.hosts.isEmpty());
    }

    static class TestConfig {
        final Long id;
        final List<String> hosts;

        public TestConfig(
                @EasyConfigProperty("ID") Long id,
                @EasyConfigProperty("HOSTS") List<String> hosts
        ) {
            this.id = id;
            this.hosts = hosts;
        }
    }

    static class DefaultTestConfig {
        final Long id;
        final List<String> hosts;

        public DefaultTestConfig(
                @EasyConfigProperty("ID") Long id,
                @EasyConfigProperty(value = "HOSTS", defaultValue = "") List<String> hosts
        ) {
            this.id = id;
            this.hosts = hosts;
        }
    }
}

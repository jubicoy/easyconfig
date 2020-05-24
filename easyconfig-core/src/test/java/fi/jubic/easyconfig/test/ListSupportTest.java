package fi.jubic.easyconfig.test;

import fi.jubic.easyconfig.ConfigMapper;
import fi.jubic.easyconfig.annotations.ConfigProperty;
import fi.jubic.easyconfig.providers.StaticEnvProvider;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ListSupportTest {
    @Test
    void listParametersMapping() {
        TestConfig config = new ConfigMapper(
                new StaticEnvProvider()
                        .with("ID", "111")
                        .with("HOSTS", "127.1.0.1;127.1.0.2")
        ).read(TestConfig.class);

        assertEquals(111L, config.id);
        assertEquals(Arrays.asList("127.1.0.1", "127.1.0.2"), config.hosts);
    }

    @Test
    void listParametersMappingThrowsWhenParameterMissing() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new ConfigMapper(
                        new StaticEnvProvider()
                                .with("ID", "111")
                ).read(TestConfig.class)
        );
    }

    @Test
    void emptyDefaultMappedToEmptyList() {
        DefaultTestConfig config = new ConfigMapper(
                new StaticEnvProvider()
                        .with("ID", "111")
        ).read(DefaultTestConfig.class);

        assertEquals(111L, config.id);
        assertTrue(config.hosts.isEmpty());
    }

    public static class TestConfig {
        final Long id;
        final List<String> hosts;

        public TestConfig(
                @ConfigProperty("ID") Long id,
                @ConfigProperty("HOSTS") List<String> hosts
        ) {
            this.id = id;
            this.hosts = hosts;
        }
    }

    public static class DefaultTestConfig {
        final Long id;
        final List<String> hosts;

        public DefaultTestConfig(
                @ConfigProperty("ID") Long id,
                @ConfigProperty(value = "HOSTS", defaultValue = "") List<String> hosts
        ) {
            this.id = id;
            this.hosts = hosts;
        }
    }
}

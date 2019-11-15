package fi.jubic.easyconfig;

import fi.jubic.easyconfig.annotations.EasyConfigProperty;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
}

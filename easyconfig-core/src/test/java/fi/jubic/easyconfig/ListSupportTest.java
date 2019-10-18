package fi.jubic.easyconfig;

import fi.jubic.easyconfig.annotations.EasyConfigProperty;
import org.junit.Test;

import java.util.*;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class ListSupportTest {
    @Test
    public void testConstructorParametersMapping() throws MappingException {
        TestConfig config = new ConfigMapper(envProvider)
                .read(TestConfig.class);

        assertThat(config.getId(), is(111L));
        assertThat(config.getHosts(), is(Arrays.asList("127.1.0.1", "127.1.0.2")));
    }

    private static EnvProvider envProvider = new StaticEnvProvider() {{
        put("ID", "111");
        put("HOSTS", "127.1.0.1;127.1.0.2");
    }};

    static class TestConfig {
        private final Long id;
        private final List<String> hosts;

        public TestConfig(
                @EasyConfigProperty("ID") Long id,
                @EasyConfigProperty("HOSTS") List<String> hosts
        ) {
            this.id = id;
            this.hosts = hosts;
        }

        Long getId() {
            return id;
        }

        List<String> getHosts() {
            return hosts;
        }
    }
}

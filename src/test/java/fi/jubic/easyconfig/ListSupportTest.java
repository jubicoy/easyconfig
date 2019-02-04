package fi.jubic.easyconfig;

import org.junit.Test;

import java.util.*;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class ListSupportTest {
    @Test
    public void testConstructorParametersMapping() throws MappingException {
        TestConfig config = new ConfigurationMapper(envProvider)
                .read(TestConfig.class);

        assertThat(config.getId(), is(111L));
        assertThat(config.getHosts(), is(Arrays.asList("127.1.0.1", "127.1.0.2")));
    }

    static EnvProvider envProvider = new EnvProvider() {
        Map<String, String> envMap = new HashMap<String, String>() {{
            put("ID", "111");
            put("HOSTS", "127.1.0.1;127.1.0.2");
        }};

        @Override
        public Optional<String> getVariable(String name) {
            if (!envMap.containsKey(name)) {
                return Optional.empty();
            }
            return Optional.of(envMap.get(name));
        }
    };

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

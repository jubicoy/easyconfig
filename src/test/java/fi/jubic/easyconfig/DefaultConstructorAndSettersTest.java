package fi.jubic.easyconfig;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.is;

public class DefaultConstructorAndSettersTest {
    @Test
    public void testDefaultConstructorAndSettersMapping() throws MappingException {
        TestConfig config = new ConfigurationMapper(envProvider)
                .read(TestConfig.class);

        assertThat(config.getId(), is(111L));
        assertThat(config.getHost(), is("127.1.0.1"));
    }

    EnvProvider envProvider = new EnvProvider() {
        Map<String, String> envMap = new HashMap<String, String>() {{
            put("ID", "111");
            put("HOST", "127.1.0.1");
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
        private Long id;
        private String host;

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

        Long getId() {
            return id;
        }

        String getHost() {
            return host;
        }
    }
}

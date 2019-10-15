package fi.jubic.easyconfig;

import fi.jubic.easyconfig.annotations.EasyConfigProperty;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class ConstructorParametersTest {
    @Test
    public void testConstructorParametersMapping() throws MappingException {
        TestConfig config = new ConfigMapper(envProvider)
                .read(TestConfig.class);

        assertThat(config.getId(), is(111L));
        assertThat(config.getHost(), is("127.1.0.1"));
    }

    static EnvProvider envProvider = new EnvProvider() {
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
        private final Long id;
        private final String host;

        public TestConfig(
                @EasyConfigProperty("ID") Long id,
                @EasyConfigProperty("HOST") String host
        ) {
            this.id = id;
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

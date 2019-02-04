package fi.jubic.easyconfig;

import org.junit.Test;

import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class DefaultValueTest {
    @Test
    public void testConstructorParametersMapping() throws MappingException {
        TestConfig config = new ConfigurationMapper(envProvider)
                .read(TestConfig.class);

        assertThat(config.getId(), is(111L));
        assertThat(config.getHost(), is("127.1.0.1"));
    }

    static EnvProvider envProvider = new EnvProvider() {
        @Override
        public Optional<String> getVariable(String name) {
            return Optional.empty();
        }
    };

    static class TestConfig {
        private final Long id;
        private final String host;

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

        Long getId() {
            return id;
        }

        String getHost() {
            return host;
        }
    }
}

package fi.jubic.easyconfig;

import fi.jubic.easyconfig.annotations.EasyConfigProperty;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class DefaultConstructorAndSettersTest {
    @Test
    public void testDefaultConstructorAndSettersMapping() throws MappingException {
        TestConfig config = new ConfigMapper(envProvider)
                .read(TestConfig.class);

        assertThat(config.id, is(111L));
        assertThat(config.host, is("127.1.0.1"));
    }

    private static EnvProvider envProvider = new StaticEnvProvider() {{
        put("ID", "111");
        put("HOST", "127.1.0.1");
    }};

    static class TestConfig {
        Long id;
        String host;

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
    }
}

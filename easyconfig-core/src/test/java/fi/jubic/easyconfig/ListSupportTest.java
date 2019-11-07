package fi.jubic.easyconfig;

import fi.jubic.easyconfig.annotations.EasyConfigProperty;

import java.util.Arrays;
import java.util.List;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class ListSupportTest {
    @Test
    public void testConstructorParametersMapping() throws MappingException {
        TestConfig config = new ConfigMapper(envProvider)
                .read(TestConfig.class);

        assertThat(config.id, is(111L));
        assertThat(config.hosts, is(Arrays.asList("127.1.0.1", "127.1.0.2")));
    }

    private static EnvProvider envProvider = new StaticEnvProvider() {{
        put("ID", "111");
        put("HOSTS", "127.1.0.1;127.1.0.2");
    }};

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

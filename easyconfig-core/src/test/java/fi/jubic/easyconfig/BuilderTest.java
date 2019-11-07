package fi.jubic.easyconfig;

import fi.jubic.easyconfig.annotations.EasyConfig;
import fi.jubic.easyconfig.annotations.EasyConfigProperty;
import fi.jubic.easyvalue.EasyProperty;
import fi.jubic.easyvalue.EasyValue;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class BuilderTest {
    @Test
    public void testConstructorParametersMapping() throws MappingException {
        TestConfig config = new ConfigMapper(envProvider)
                .read(TestConfig.class);

        assertThat(config.id(), is(111L));
        assertThat(config.host(), is("127.1.0.1"));
    }

    private static EnvProvider envProvider = new StaticEnvProvider() {{
        put("ID", "111");
        put("HOST", "127.1.0.1");
    }};

    @EasyConfig(builder = TestConfig.Builder.class)
    @EasyValue(excludeJson = true)
    abstract static class TestConfig {
        @EasyProperty
        abstract Long id();

        @EasyProperty
        abstract String host();

        static Builder builder() {
            return new Builder();
        }


        static class Builder extends EasyValue_BuilderTest_TestConfig.Builder {
            @EasyConfigProperty("ID")
            @Override
            public Builder setId(Long id) {
                return super.setId(id);
            }

            @EasyConfigProperty("HOST")
            @Override
            public Builder setHost(String host) {
                return super.setHost(host);
            }
        }
    }
}

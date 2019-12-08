package fi.jubic.easyconfig;

import fi.jubic.easyconfig.annotations.EasyConfig;
import fi.jubic.easyconfig.annotations.EasyConfigProperty;
import fi.jubic.easyvalue.EasyProperty;
import fi.jubic.easyvalue.EasyValue;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BuilderTest {
    @Test
    void testConstructorParametersMapping() throws MappingException {
        TestConfig config = new ConfigMapper(envProvider)
                .read(TestConfig.class);

        assertEquals(111L, config.id());
        assertEquals("127.1.0.1", config.host());
    }

    private static EnvProvider envProvider = new StaticEnvProvider()
            .with("ID", "111")
            .with("HOST", "127.1.0.1");

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

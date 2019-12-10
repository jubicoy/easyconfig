package fi.jubic.easyconfig;

import fi.jubic.easyconfig.annotations.EasyConfig;
import fi.jubic.easyconfig.annotations.EasyConfigProperty;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BuilderTest {
    @Test
    void testConstructorParametersMapping() throws MappingException {
        TestConfig config = new ConfigMapper(envProvider)
                .read(TestConfig.class);

        assertEquals(111L, config.getId());
        assertEquals("127.1.0.1", config.getHost());
    }

    private static EnvProvider envProvider = new StaticEnvProvider()
            .with("ID", "111")
            .with("HOST", "127.1.0.1");

    @EasyConfig(builder = TestConfig.Builder.class)
    static class TestConfig {
        private final Long id;
        private final String host;

        private TestConfig(
                Long id,
                String host
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

        static class Builder {
            private final Long id;
            private final String host;

            public Builder() {
                this.id = null;
                this.host = null;
            }

            private Builder(
                    Long id,
                    String host
            ) {
                this.id = id;
                this.host = host;
            }

            @EasyConfigProperty("ID")
            public Builder setId(Long id) {
                return new Builder(id, this.host);
            }

            @EasyConfigProperty("HOST")
            public Builder setHost(String host) {
                return new Builder(this.id, host);
            }

            public TestConfig build() {
                return new TestConfig(id, host);
            }
        }
    }
}

package fi.jubic.easyconfig;

import fi.jubic.easyconfig.annotations.ConfigProperty;
import fi.jubic.easyconfig.annotations.EasyConfig;
import fi.jubic.easyconfig.annotations.EasyConfigProperty;
import fi.jubic.easyconfig.annotations.EnvProviderProperty;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class BuilderTest {
    @Test
    void testConstructorParametersMapping() throws MappingException {
        TestConfig config = new ConfigMapper(envProvider)
                .read(TestConfig.class);

        assertEquals(111L, config.getId());
        assertEquals("127.1.0.1", config.getHost());
    }

    @Test
    void testConstructorParametersEnvProviderMapping() throws MappingException {
        TestConfig2 config = new ConfigMapper(envProvider)
                .read(TestConfig2.class);

        assertEquals(111L, config.getId());
        assertNotNull(config.getEnvProvider());
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

    @EasyConfig(builder = TestConfig2.Builder.class)
    static class TestConfig2 {
        private final Long id;
        private final EnvProvider envProvider;

        private TestConfig2(
                Long id,
                EnvProvider envProvider
        ) {
            this.id = id;
            this.envProvider = envProvider;
        }

        Long getId() {
            return id;
        }

        EnvProvider getEnvProvider() {
            return envProvider;
        }

        static class Builder {
            private final Long id;
            private final EnvProvider envProvider;

            public Builder() {
                this.id = null;
                this.envProvider = null;
            }

            private Builder(
                    Long id,
                    EnvProvider envProvider
            ) {
                this.id = id;
                this.envProvider = envProvider;
            }

            @ConfigProperty("ID")
            public Builder setId(Long id) {
                return new Builder(id, this.envProvider);
            }

            @EnvProviderProperty
            public Builder setEnvProvider(EnvProvider envProvider) {
                return new Builder(this.id, envProvider);
            }

            public TestConfig2 build() {
                return new TestConfig2(id, envProvider);
            }
        }
    }
}

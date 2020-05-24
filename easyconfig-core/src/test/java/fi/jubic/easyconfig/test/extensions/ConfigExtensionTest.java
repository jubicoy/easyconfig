package fi.jubic.easyconfig.test.extensions;

import fi.jubic.easyconfig.ConfigMapper;
import fi.jubic.easyconfig.annotations.ConfigProperty;
import fi.jubic.easyconfig.extensions.ConfigExtension;
import fi.jubic.easyconfig.extensions.ConfigExtensionProvider;
import fi.jubic.easyconfig.providers.EnvProvider;
import fi.jubic.easyconfig.providers.StaticEnvProvider;
import org.junit.jupiter.api.Test;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ConfigExtensionTest {
    @Test
    void shouldExtendConfiguration() {
        EnvProvider envProvider = new StaticEnvProvider()
                .with("VALUE", "value")
                .with("PROVIDER_VALUE", "provider_value");

        assertEquals(
                "value test_value provider_value",
                new ConfigMapper(envProvider)
                        .read(TestRootConfig.class)
                        .testConfig
                        .value
        );
    }

    public static class TestRootConfig {
        private TestConfig testConfig;

        public TestRootConfig(
                @TestExtension(test = "test_value")
                @ConfigProperty("") TestConfig testConfig
        ) {
            this.testConfig = testConfig;
        }
    }

    public static class TestConfig {
        private final String value;

        public TestConfig(
                @ConfigProperty("VALUE") String value
        ) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    @ConfigExtension(TestExtensionProvider.class)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface TestExtension {
        String test();
    }

    public static class TestExtensionProvider
            implements ConfigExtensionProvider<TestExtension, TestConfig> {
        private final String providerValue;

        public TestExtensionProvider(
                @ConfigProperty("PROVIDER_VALUE") String providerValue
        ) {
            this.providerValue = providerValue;
        }

        @Override
        public TestConfig extend(TestExtension extensionParams, TestConfig configuration) {
            return new TestConfig(
                    String.join(
                            " ",
                            configuration.getValue(),
                            extensionParams.test(),
                            providerValue
                    )
            );
        }
    }
}

package fi.jubic.easyconfig.test;

import fi.jubic.easyconfig.ConfigMapper;
import fi.jubic.easyconfig.annotations.ConfigProperty;
import fi.jubic.easyconfig.providers.EnvProvider;
import fi.jubic.easyconfig.providers.StaticEnvProvider;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ErrorMessageTest {
    @Test
    void shouldInformAboutMissingVariables() {
        EnvProvider envProvider = new StaticEnvProvider()
                .with("VALUE_A", "value");

        String message = "";
        try {
            new ConfigMapper(envProvider).read(ParentTestConfig.class);
        }
        catch (IllegalArgumentException e) {
            message = e.getMessage();
        }

        assertTrue(message.contains("Missing variable \"VALUE_B\" [String]"));
        assertTrue(message.contains("Missing variable \"VALUE_C\" [int]"));
        assertFalse(message.contains("\"VALUE_A\""));
        assertFalse(message.contains("\"VALUE_D\""));
    }

    public static class ParentTestConfig {
        final TestConfig testConfig;

        public ParentTestConfig(
                @ConfigProperty("VALUE_") TestConfig testConfig
        ) {
            this.testConfig = testConfig;
        }
    }

    public static class TestConfig {
        final String valueA;
        final String valueB;
        final int valueC;
        final String valueD;

        public TestConfig(
                @ConfigProperty("A") String valueA,
                @ConfigProperty("B") String valueB,
                @ConfigProperty("C") int valueC,
                @ConfigProperty(value = "D", defaultValue = "") String valueD
        ) {
            this.valueA = valueA;
            this.valueB = valueB;
            this.valueC = valueC;
            this.valueD = valueD;
        }
    }
}

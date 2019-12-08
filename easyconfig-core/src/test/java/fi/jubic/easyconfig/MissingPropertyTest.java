package fi.jubic.easyconfig;

import fi.jubic.easyconfig.annotations.EasyConfigProperty;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MissingPropertyTest {
    @Test
    void testExceptionMessageContainsEnvVariableName() {
        EnvProvider envProvider = new StaticEnvProvider()
                .with("VALUE_A", "value");

        String message = "";
        try {
            new ConfigMapper(envProvider).read(ParentTestConfig.class);
        }
        catch (MappingException e) {
            message = e.getMessage();
        }

        assertTrue(message.contains("VALUE_B [String]"));
        assertTrue(message.contains("VALUE_C [int]"));
        assertFalse(message.contains("VALUE_A"));
        assertFalse(message.contains("VALUE_D"));
    }

    static class ParentTestConfig {
        final TestConfig testConfig;

        public ParentTestConfig(
                @EasyConfigProperty("VALUE_") TestConfig testConfig
        ) {
            this.testConfig = testConfig;
        }
    }

    static class TestConfig {
        final String valueA;
        final String valueB;
        final int valueC;
        final String valueD;

        public TestConfig(
                @EasyConfigProperty("A") String valueA,
                @EasyConfigProperty("B") String valueB,
                @EasyConfigProperty("C") int valueC,
                @EasyConfigProperty(value = "D", defaultValue = "") String valueD
        ) {
            this.valueA = valueA;
            this.valueB = valueB;
            this.valueC = valueC;
            this.valueD = valueD;
        }
    }
}

package fi.jubic.easyconfig;

import fi.jubic.easyconfig.annotations.EasyConfigProperty;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;

public class MissingPropertyTest {
    @Test
    public void testExceptionMessageContainsEnvVariableName() {
        EnvProvider envProvider = new StaticEnvProvider() {{
            put("VALUE_A", "value");
        }};

        String message = "";
        try {
            new ConfigMapper(envProvider).read(ParentTestConfig.class);
        } catch (MappingException e) {
            message = e.getMessage();
        }

        System.out.println(message);
        assertThat(message, containsString("VALUE_B [String]"));
        assertThat(message, containsString("VALUE_C [int]"));
        assertThat(message, not(containsString("VALUE_A")));
        assertThat(message, not(containsString("VALUE_D")));
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

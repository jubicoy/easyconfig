package fi.jubic.easyconfig.test;

import fi.jubic.easyconfig.ConfigMapper;
import fi.jubic.easyconfig.annotations.ConfigProperty;
import fi.jubic.easyconfig.providers.StaticEnvProvider;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

class InitializationErrorTest {
    @Test
    void shouldReportMissingVariables() {
        String message = "";
        try {
            new ConfigMapper(
                    new StaticEnvProvider()
                            .with("PARENT_VALUE_A", "value")
            ).read(MissingVariablesGrandParentTestConfig.class);
            fail("Should have thrown");
        }
        catch (IllegalArgumentException e) {
            message = e.getMessage();
        }

        assertTrue(message.contains("Missing variable \"TOP_LEVEL\" [String]"));
        assertTrue(message.contains("Missing variable \"PARENT_VALUE_B\" [String]"));
        assertTrue(message.contains("Missing variable \"PARENT_VALUE_C\" [int]"));
        assertFalse(message.contains("VALUE_A"));
        assertFalse(message.contains("VALUE_D"));
    }

    public static class MissingVariablesGrandParentTestConfig {
        public MissingVariablesGrandParentTestConfig(
                @ConfigProperty("PARENT_") MissingVariablesParentTestConfig parentConfig,
                @ConfigProperty("TOP_LEVEL") String topLevel
        ) {
        }
    }

    public static class MissingVariablesParentTestConfig {
        public MissingVariablesParentTestConfig(
                @ConfigProperty("VALUE_") MissingVariablesTestConfig testConfig
        ) {
        }
    }

    public static class MissingVariablesTestConfig {
        public MissingVariablesTestConfig(
                @ConfigProperty("A") String valueA,
                @ConfigProperty("B") String valueB,
                @ConfigProperty("C") int valueC,
                @ConfigProperty(value = "D", defaultValue = "") String valueD
        ) {
        }
    }

    @Test
    void shouldAllowNullableValues() {
        String message = "";
        try {
            new ConfigMapper(new StaticEnvProvider()).read(NullableTestConfig.class);
            fail("Should have thrown");
        }
        catch (IllegalArgumentException exception) {
            message = exception.getMessage();
        }

        System.out.println(message);
        assertTrue(message.contains("Invalid configuration parameter"));
        assertTrue(message.contains("Missing variable \"NONNULL\" [String]"));
        assertFalse(message.contains("NULLABLE"));
    }

    public static class NullableTestConfig {
        public NullableTestConfig(
                @ConfigProperty(value = "NULLABLE", nullable = true) String nullableStr,
                @ConfigProperty(value = "NONNULL") String nonNullableString
        ) {
        }
    }

    @Test
    void shouldReportPrimitiveParsingIssues() {
        String message = "";
        try {
            new ConfigMapper(
                    new StaticEnvProvider()
                            .with("BOOL", "not bool")
                            .with("INT", "not int")
                            .with("LONG", "not long")
                            .with("FLOAT", "not float")
                            .with("DOUBLE", "not double")
            ).read(PrimitiveParsingErrorConfig.class);
            fail("Should have thrown");
        }
        catch (IllegalArgumentException exception) {
            message = exception.getMessage();
        }

        assertTrue(message.contains("\"BOOL\" [boolean]: Could not parse \"not bool\""));
        assertTrue(message.contains("\"INT\" [int]: Could not parse \"not int\""));
        assertTrue(message.contains("\"LONG\" [int]: Could not parse \"not long\""));
        assertTrue(message.contains("\"FLOAT\" [float]: Could not parse \"not float\""));
        assertTrue(message.contains("\"DOUBLE\" [double]: Could not parse \"not double\""));
    }

    public static class PrimitiveParsingErrorConfig {
        public PrimitiveParsingErrorConfig(
                @ConfigProperty("BOOL") boolean boolVal,
                @ConfigProperty("INT") int intVal,
                @ConfigProperty("LONG") int longVal,
                @ConfigProperty("FLOAT") float floatVal,
                @ConfigProperty("DOUBLE") double doubleVal
        ) {
        }
    }

    @Test
    void shouldReportPrimitiveParsingIssuesInPrimitiveLists() {
        String message = "";
        try {
            new ConfigMapper(
                    new StaticEnvProvider()
                            .with("LIST", "invalid;value")
            ).read(PrimitiveListParsingErrorConfig.class);
            fail("Should have thrown");
        }
        catch (IllegalArgumentException exception) {
            message = exception.getMessage();
        }

        assertTrue(message.contains("\"LIST\" [List<Integer>]: Invalid value"));
        assertTrue(message.contains("Could not parse \"invalid\" in \"invalid;value\""));
        assertTrue(message.contains("Could not parse \"value\" in \"invalid;value\""));
    }

    public static class PrimitiveListParsingErrorConfig {
        public PrimitiveListParsingErrorConfig(
                @ConfigProperty("LIST")List<Integer> intList
        ) {
        }
    }

    @Test
    void shouldReportParsingIssuesInNestedLists() {
        String message = "";
        try {
            new ConfigMapper(
                    new StaticEnvProvider()
                            .with("OBJ_0_VAL", "1")
                            .with("OBJ_1_VAL", "not_int")
                            .with("OBJ_2_VAL", "3")
                            .with("OBJ_3_VAL", "not int either")
            ).read(NestedListParsingErrorConfig.class);
            fail("Should have thrown");
        }
        catch (IllegalArgumentException exception) {
            message = exception.getMessage();
        }

        assertTrue(message.contains("Invalid configuration parameter"));
        assertTrue(message.contains("\"OBJ_1_VAL\" [int]: Could not parse \"not_int\""));
        assertTrue(message.contains("\"OBJ_3_VAL\" [int]: Could not parse \"not int either\""));
    }

    public static class NestedListParsingErrorConfig {
        public NestedListParsingErrorConfig(
                @ConfigProperty("OBJ_{}_") List<NestedListElementConfig> elements
        ) {
        }
    }

    public static class NestedListElementConfig {
        public NestedListElementConfig(
                @ConfigProperty("VAL") int val
        ) {
        }
    }
}

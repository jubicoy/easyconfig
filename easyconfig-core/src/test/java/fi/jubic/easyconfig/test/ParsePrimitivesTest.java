package fi.jubic.easyconfig.test;

import fi.jubic.easyconfig.ConfigMapper;
import fi.jubic.easyconfig.annotations.ConfigProperty;
import fi.jubic.easyconfig.providers.EnvProvider;
import fi.jubic.easyconfig.providers.StaticEnvProvider;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ParsePrimitivesTest {
    @Test
    void testDefaultConstructorMapping() {
        EnvProvider envProvider = new StaticEnvProvider()
                .with("BOOL_1", "true")
                .with("BOOL_2", "false")
                .with("INTEGER_1", "12")
                .with("INTEGER_2", "13")
                .with("LONG_1", "1001")
                .with("LONG_2", "1002")
                .with("FLOAT_1", "1.23")
                .with("FLOAT_2", "2.34")
                .with("DOUBLE_1", "3.45")
                .with("DOUBLE_2", "4.56")
                .with("STRING", "qwerty");

        DefaultConstructorTestConfig config = new ConfigMapper(envProvider)
                .read(DefaultConstructorTestConfig.class);

        assertTrue(config.bool1);
        assertFalse(config.bool2);
        assertEquals(12, config.integer1);
        assertEquals(13, config.integer2);
        assertEquals(1001L, config.long1);
        assertEquals(1002L, config.long2);
        assertEquals(1.23F, config.float1);
        assertEquals(2.34F, config.float2);
        assertEquals(3.45, config.double1);
        assertEquals(4.56, config.double2);
        assertEquals("qwerty", config.string);
    }

    public static class DefaultConstructorTestConfig {
        private boolean bool1;
        private Boolean bool2;
        private int integer1;
        private Integer integer2;
        private long long1;
        private Long long2;
        private float float1;
        private Float float2;
        private double double1;
        private Double double2;
        private String string;

        public DefaultConstructorTestConfig() {
        }

        @ConfigProperty(value = "BOOL_1")
        public void setBool1(boolean bool1) {
            this.bool1 = bool1;
        }

        @ConfigProperty(value = "BOOL_2")
        public void setBool2(Boolean bool2) {
            this.bool2 = bool2;
        }

        @ConfigProperty(value = "INTEGER_1")
        public void setInteger1(int integer1) {
            this.integer1 = integer1;
        }

        @ConfigProperty(value = "INTEGER_2")
        public void setInteger2(Integer integer2) {
            this.integer2 = integer2;
        }

        @ConfigProperty(value = "LONG_1")
        public void setLong1(long long1) {
            this.long1 = long1;
        }

        @ConfigProperty(value = "LONG_2")
        public void setLong2(Long long2) {
            this.long2 = long2;
        }

        @ConfigProperty(value = "FLOAT_1")
        public void setFloat1(float float1) {
            this.float1 = float1;
        }

        @ConfigProperty(value = "FLOAT_2")
        public void setFloat2(Float float2) {
            this.float2 = float2;
        }

        @ConfigProperty(value = "DOUBLE_1")
        public void setDouble1(double double1) {
            this.double1 = double1;
        }

        @ConfigProperty(value = "DOUBLE_2")
        public void setDouble2(Double double2) {
            this.double2 = double2;
        }

        @ConfigProperty(value = "STRING")
        public void setString(String string) {
            this.string = string;
        }
    }
}

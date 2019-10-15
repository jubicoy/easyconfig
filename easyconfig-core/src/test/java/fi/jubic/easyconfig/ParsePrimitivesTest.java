package fi.jubic.easyconfig;

import fi.jubic.easyconfig.annotations.EasyConfig;
import fi.jubic.easyconfig.annotations.EasyConfigProperty;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.is;

public class ParsePrimitivesTest {

    @Test
    public void testDefaultConstructorMapping() throws MappingException {
        EnvProvider envProvider = new EnvProvider() {
            Map<String, String> envMap = new HashMap<String, String>() {{
                put("BOOL_1", "true");
                put("BOOL_2", "false");
                put("INTEGER_1", "12");
                put("INTEGER_2", "13");
                put("LONG_1", "1001");
                put("LONG_2", "1002");
                put("FLOAT_1", "1.23");
                put("FLOAT_2", "2.34");
                put("DOUBLE_1", "3.45");
                put("DOUBLE_2", "4.56");
                put("STRING", "qwerty");
            }};

            @Override
            public Optional<String> getVariable(String name) {
                if (!envMap.containsKey(name)) {
                    return Optional.empty();
                }
                return Optional.of(envMap.get(name));
            }
        };

        DefaultConstructorTestConfig config = new ConfigMapper(envProvider).read(DefaultConstructorTestConfig.class);

        assertThat(config.isBool1(), is(true));
        assertThat(config.getBool2(), is(false));
        assertThat(config.getInteger1(), is(12));
        assertThat(config.getInteger2(), is(13));
        assertThat(config.getLong1(), is(1001L));
        assertThat(config.getLong2(), is(1002L));
        assertThat(config.getFloat1(), is(1.23F));
        assertThat(config.getFloat2(), is(2.34F));
        assertThat(config.getDouble1(), is(3.45));
        assertThat(config.getDouble2(), is(4.56));
        assertThat(config.getString(), is("qwerty"));
    }

    @EasyConfig
    static class DefaultConstructorTestConfig {
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

        @EasyConfigProperty(value = "BOOL_1")
        public void setBool1(boolean bool1) {
            this.bool1 = bool1;
        }

        @EasyConfigProperty(value = "BOOL_2")
        public void setBool2(Boolean bool2) {
            this.bool2 = bool2;
        }

        @EasyConfigProperty(value = "INTEGER_1")
        public void setInteger1(int integer1) {
            this.integer1 = integer1;
        }

        @EasyConfigProperty(value = "INTEGER_2")
        public void setInteger2(Integer integer2) {
            this.integer2 = integer2;
        }

        @EasyConfigProperty(value = "LONG_1")
        public void setLong1(long long1) {
            this.long1 = long1;
        }

        @EasyConfigProperty(value = "LONG_2")
        public void setLong2(Long long2) {
            this.long2 = long2;
        }

        @EasyConfigProperty(value = "FLOAT_1")
        public void setFloat1(float float1) {
            this.float1 = float1;
        }

        @EasyConfigProperty(value = "FLOAT_2")
        public void setFloat2(Float float2) {
            this.float2 = float2;
        }

        @EasyConfigProperty(value = "DOUBLE_1")
        public void setDouble1(double double1) {
            this.double1 = double1;
        }

        @EasyConfigProperty(value = "DOUBLE_2")
        public void setDouble2(Double double2) {
            this.double2 = double2;
        }

        @EasyConfigProperty(value = "STRING")
        public void setString(String string) {
            this.string = string;
        }

        boolean isBool1() {
            return bool1;
        }

        Boolean getBool2() {
            return bool2;
        }

        int getInteger1() {
            return integer1;
        }

        Integer getInteger2() {
            return integer2;
        }

        long getLong1() {
            return long1;
        }

        Long getLong2() {
            return long2;
        }

        float getFloat1() {
            return float1;
        }

        Float getFloat2() {
            return float2;
        }

        double getDouble1() {
            return double1;
        }

        Double getDouble2() {
            return double2;
        }

        String getString() {
            return string;
        }
    }
}

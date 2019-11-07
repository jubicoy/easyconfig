package fi.jubic.easyconfig;

import fi.jubic.easyconfig.annotations.EasyConfig;
import fi.jubic.easyconfig.annotations.EasyConfigProperty;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class ParsePrimitivesTest {

    @Test
    public void testDefaultConstructorMapping() throws MappingException {
        EnvProvider envProvider = new StaticEnvProvider() {{
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

        DefaultConstructorTestConfig config = new ConfigMapper(envProvider)
                .read(DefaultConstructorTestConfig.class);

        assertThat(config.bool1, is(true));
        assertThat(config.bool2, is(false));
        assertThat(config.integer1, is(12));
        assertThat(config.integer2, is(13));
        assertThat(config.long1, is(1001L));
        assertThat(config.long2, is(1002L));
        assertThat(config.float1, is(1.23F));
        assertThat(config.float2, is(2.34F));
        assertThat(config.double1, is(3.45));
        assertThat(config.double2, is(4.56));
        assertThat(config.string, is("qwerty"));
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
    }
}

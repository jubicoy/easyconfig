package fi.jubic.easyconfig;

import fi.jubic.easyconfig.annotations.EasyConfigProperty;
import io.github.cdimascio.dotenv.Dotenv;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DotenvTest {
    @Test
    void testDotenvSupport() throws MappingException {
        EnvProvider envProvider = new DotenvProvider(
                Dotenv.configure()
                        .ignoreIfMissing()
                        .directory("src/test/resources")
                        .load()
        );

        TestConfig config = new ConfigMapper(envProvider).read(TestConfig.class);

        assertEquals("set by dotenv", config.dotenvValue);
    }

    static class TestConfig {
        final String dotenvValue;

        public TestConfig(
                @EasyConfigProperty("DOTENV_VALUE") String dotenvValue
        ) {
            this.dotenvValue = dotenvValue;
        }
    }
}

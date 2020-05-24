package fi.jubic.easyconfig.test;

import fi.jubic.easyconfig.ConfigMapper;
import fi.jubic.easyconfig.annotations.ConfigProperty;
import fi.jubic.easyconfig.providers.DotenvProvider;
import fi.jubic.easyconfig.providers.EnvProvider;
import io.github.cdimascio.dotenv.Dotenv;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DotenvTest {
    @Test
    void testDotenvSupport() {
        EnvProvider envProvider = new DotenvProvider(
                Dotenv.configure()
                        .ignoreIfMissing()
                        .directory("src/test/resources")
                        .load()
        );

        TestConfig config = new ConfigMapper(envProvider).read(TestConfig.class);

        assertEquals("set by dotenv", config.dotenvValue);
    }

    public static class TestConfig {
        final String dotenvValue;

        public TestConfig(
                @ConfigProperty("DOTENV_VALUE") String dotenvValue
        ) {
            this.dotenvValue = dotenvValue;
        }
    }
}

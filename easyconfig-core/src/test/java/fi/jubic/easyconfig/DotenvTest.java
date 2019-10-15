package fi.jubic.easyconfig;

import fi.jubic.easyconfig.annotations.EasyConfig;
import fi.jubic.easyconfig.annotations.EasyConfigProperty;
import io.github.cdimascio.dotenv.Dotenv;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class DotenvTest {
    @Test
    public void testDotenvSupport() throws MappingException {
        EnvProvider envProvider = new DotenvProvider(
                Dotenv.configure()
                        .ignoreIfMissing()
                        .directory("src/test/resources")
                        .load()
        );

        TestConfig config = new ConfigMapper(envProvider).read(TestConfig.class);

        assertThat(config.getDotenvValue(), is("set by dotenv"));
    }

    @EasyConfig
    static class TestConfig {
        private final String dotenvValue;

        public TestConfig(
                @EasyConfigProperty("DOTENV_VALUE") String dotenvValue
        ) {
            this.dotenvValue = dotenvValue;
        }

        String getDotenvValue() {
            return dotenvValue;
        }
    }
}

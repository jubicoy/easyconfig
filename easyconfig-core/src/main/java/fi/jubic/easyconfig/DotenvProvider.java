package fi.jubic.easyconfig;

import io.github.cdimascio.dotenv.Dotenv;

import java.util.Optional;

public class DotenvProvider implements EnvProvider {
    private final Dotenv dotenv;

    DotenvProvider() {
        String dotenvDir = System.getenv("EASYCONFIG_DOTENV_DIR");
        if (dotenvDir != null) {
            dotenv = Dotenv.configure()
                    .ignoreIfMissing()
                    .directory(dotenvDir)
                    .load();
        }
        else {
            dotenv = Dotenv.configure()
                    .ignoreIfMissing()
                    .load();
        }
    }

    DotenvProvider(Dotenv dotenv) {
        this.dotenv = dotenv;
    }

    @Override
    public Optional<String> getVariable(String name) {
        return Optional.ofNullable(dotenv.get(name));
    }
}

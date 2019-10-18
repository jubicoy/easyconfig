package fi.jubic.easyconfig;

import io.github.cdimascio.dotenv.Dotenv;

import java.util.Optional;

public class DotenvProvider extends EnvProvider {
    private final Dotenv dotenv;

    DotenvProvider() {
        super("");

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
        this(dotenv, "");
    }

    DotenvProvider(Dotenv dotenv, String prefix) {
        super(prefix);
        this.dotenv = dotenv;
    }

    @Override
    public EnvProvider withPrefix(String prefix) {
        return new DotenvProvider(this.dotenv, prefix + this.prefix());
    }

    @Override
    public Optional<String> getVariable(String name) {
        return Optional.ofNullable(dotenv.get(prefix() + name));
    }
}

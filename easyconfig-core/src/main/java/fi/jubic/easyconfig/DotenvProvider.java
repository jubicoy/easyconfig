package fi.jubic.easyconfig;

import io.github.cdimascio.dotenv.Dotenv;
import io.github.cdimascio.dotenv.DotenvEntry;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

    private DotenvProvider(Dotenv dotenv, String prefix) {
        super(prefix);
        this.dotenv = dotenv;
    }

    @Override
    public EnvProvider withPrefix(String prefix) {
        return new DotenvProvider(this.dotenv, this.prefix() + prefix);
    }

    @Override
    public Optional<String> getVariable(String name) {
        return Optional.ofNullable(dotenv.get(prefix() + name));
    }

    @Override
    protected Stream<String> getNames() {
        return dotenv.entries()
                .stream()
                .map(DotenvEntry::getKey);
    }

    @Override
    public Map<String, String> getVariables() {
        return dotenv.entries()
                .stream()
                .collect(Collectors.toMap(
                        DotenvEntry::getKey,
                        DotenvEntry::getValue
                ));
    }
}

package fi.jubic.easyconfig.providers;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.github.cdimascio.dotenv.Dotenv;
import io.github.cdimascio.dotenv.DotenvEntry;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@SuppressFBWarnings("CT_CONSTRUCTOR_THROW")
public class DotenvProvider extends EnvProvider {
    private final Dotenv dotenv;

    public DotenvProvider() {
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

    public DotenvProvider(Dotenv dotenv) {
        this.dotenv = dotenv;
    }

    @Override
    public Optional<String> getVariable(String name) {
        return Optional.ofNullable(dotenv.get(name));
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

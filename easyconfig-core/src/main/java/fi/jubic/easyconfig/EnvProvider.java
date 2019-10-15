package fi.jubic.easyconfig;

import java.util.Optional;

public interface EnvProvider {

    Optional<String> getVariable(String name);

    static EnvProvider getDefault() {
        return new DotenvProvider();
    }
}

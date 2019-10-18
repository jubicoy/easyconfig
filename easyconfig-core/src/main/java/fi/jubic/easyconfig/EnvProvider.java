package fi.jubic.easyconfig;

import java.util.Optional;

public abstract class EnvProvider {
    private final String prefix;

    public EnvProvider(String prefix) {
        this.prefix = prefix;
    }

    public String prefix() {
        return prefix;
    }

    public abstract EnvProvider withPrefix(String prefix);

    public abstract Optional<String> getVariable(String name);

    static EnvProvider getDefault() {
        return new DotenvProvider();
    }
}

package fi.jubic.easyconfig;

import java.util.Optional;

public interface EnvProvider {

    Optional<String> getVariable(String name);

    static EnvProvider getDefault() {
        return new DefaultProvider();
    }

    class DefaultProvider implements EnvProvider {
        @Override
        public Optional<String> getVariable(String name) {
            return Optional.ofNullable(System.getenv(name));
        }
    }
}

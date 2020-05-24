package fi.jubic.easyconfig.providers;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * A {@link Map}-backed provider for testing purposes.
 */
public class StaticEnvProvider extends EnvProvider {
    private final Map<String, String> varMap;

    public StaticEnvProvider() {
        this.varMap = new HashMap<>();
    }

    private void put(String varName, String value) {
        varMap.put(varName, value);
    }

    public StaticEnvProvider with(String varName, String value) {
        put(varName, value);
        return this;
    }

    @Override
    public Optional<String> getVariable(String name) {
        if (!varMap.containsKey(name)) {
            return Optional.empty();
        }
        return Optional.of(varMap.get(name));
    }

    @Override
    protected Stream<String> getNames() {
        return varMap.keySet().stream();
    }

    @Override
    public Map<String, String> getVariables() {
        return Collections.unmodifiableMap(varMap);
    }
}

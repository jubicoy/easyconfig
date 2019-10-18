package fi.jubic.easyconfig;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

public class StaticEnvProvider extends EnvProvider {
    private final Map<String, String> varMap;

    public StaticEnvProvider() {
        this("");
    }

    private StaticEnvProvider(String prefix) {
        super(prefix);
        this.varMap = new HashMap<>();
    }

    private StaticEnvProvider(String prefix, Map<String, String> varMap) {
        super(prefix);
        this.varMap = varMap;
    }

    public void put(String varName, String value) {
        varMap.put(varName, value);
    }

    @Override
    public EnvProvider withPrefix(String prefix) {
        return new StaticEnvProvider(prefix, varMap);
    }

    @Override
    public Optional<String> getVariable(String name) {
        if (!varMap.containsKey(prefix() + name)) {
            return Optional.empty();
        }
        return Optional.of(varMap.get(prefix() + name));
    }

    @Override
    protected Stream<String> getNames() {
        return varMap.keySet().stream();
    }
}

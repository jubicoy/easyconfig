package fi.jubic.easyconfig.providers;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Environment variable provider for custom or testing implementations.
 */
public abstract class EnvProvider {
    /**
     * Returns an {@link Optional} of the environment variable value if a variable with the given
     * name is found. Otherwise returns en empty {@link Optional}.
     *
     * @param name the environment variable name to query
     * @return {@link Optional} of the variable value if found, otherwise {@link Optional#empty()}
     */
    public abstract Optional<String> getVariable(String name);

    /**
     * Return all available environment variables.
     *
     * @return the variables as a name-value map
     */
    public abstract Map<String, String> getVariables();

    public Stream<String> getKeysMatching(String nameTemplate) {
        Pattern pattern = Pattern.compile(
                nameTemplate.replace("{}", "(\\d+)") + ".+"
        );
        return getNames()
                .map(pattern::matcher)
                .filter(Matcher::find)
                .map(matcher -> matcher.group(1))
                .distinct()
                .sorted();
    }

    protected abstract Stream<String> getNames();

    /**
     * Return added or modified entries this provider offers compared to {@link System#getenv()}.
     *
     * @return the diff as a map
     */
    public Map<String, String> getAdditionalVariables() {
        Map<String, String> systemEnv = System.getenv();
        return getVariables()
                .entrySet()
                .stream()
                .filter(entry -> !systemEnv.containsKey(entry.getKey())
                        || !Objects.equals(systemEnv.get(entry.getKey()), entry.getValue())
                )
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue
                ));
    }

    public static EnvProvider getDefault() {
        return new DotenvProvider();
    }
}

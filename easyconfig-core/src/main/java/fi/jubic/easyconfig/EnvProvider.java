package fi.jubic.easyconfig;

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
    private final String prefix;

    /**
     * Construct a new {@code EnvProvider} with the given prefix. All environment variables
     * requested through the created provider will prepend variable queries with its prefix.
     *
     * <p>
     *     The prefix does not have an effect on {@link EnvProvider#getVariables()} and
     *     {@link EnvProvider#getAdditionalVariables()} calls.
     * </p>
     *
     * @param prefix the prefix to use when querying environment variables
     */
    public EnvProvider(String prefix) {
        this.prefix = prefix;
    }

    String prefix() {
        return prefix;
    }

    /**
     * Create a new {@code EnvProvider} appending the given prefix to this provider's prefix.
     *
     * @param prefix the prefix to append
     * @return the created {@code EnvProvider}
     */
    public abstract EnvProvider withPrefix(String prefix);

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

    Stream<String> getKeysMatching(String nameTemplate) {
        Pattern pattern = Pattern.compile(
                prefix() + nameTemplate.replace("{}", "(\\d+)") + ".+"
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
     * Return added or modifed entries this provider offers compared to {@link System#getenv()}.
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

    static EnvProvider getDefault() {
        return new DotenvProvider();
    }
}

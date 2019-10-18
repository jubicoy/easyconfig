package fi.jubic.easyconfig;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public abstract class EnvProvider {
    private final String prefix;

    public EnvProvider(String prefix) {
        this.prefix = prefix;
    }

    String prefix() {
        return prefix;
    }

    public abstract EnvProvider withPrefix(String prefix);

    public abstract Optional<String> getVariable(String name);

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

    static EnvProvider getDefault() {
        return new DotenvProvider();
    }
}

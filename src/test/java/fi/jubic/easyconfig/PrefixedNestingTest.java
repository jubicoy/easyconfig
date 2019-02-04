package fi.jubic.easyconfig;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class PrefixedNestingTest {
    @Test
    public void testPrefixedNesting() throws MappingException {
        ParentConfig parent = new ConfigurationMapper(envProvider)
                .read(ParentConfig.class);

        assertThat(parent.getId(), is(111L));
        assertThat(parent.getChild().getId(), is(112L));
    }

    static EnvProvider envProvider = new EnvProvider() {
        Map<String, String> envMap = new HashMap<String, String>() {{
            put("ID", "111");
            put("CHILD_ID", "112");
        }};

        @Override
        public Optional<String> getVariable(String name) {
            if (!envMap.containsKey(name)) {
                return Optional.empty();
            }
            return Optional.of(envMap.get(name));
        }
    };

    static class ParentConfig {
        private final Long id;
        private final ChildConfig child;

        public ParentConfig(
                @EasyConfigProperty("ID") Long id,
                @EasyConfigProperty("CHILD_") ChildConfig child
        ) {
            this.id = id;
            this.child = child;
        }

        Long getId() {
            return id;
        }

        ChildConfig getChild() {
            return child;
        }
    }

    static class ChildConfig {
        private final Long id;

        public ChildConfig(
                @EasyConfigProperty("ID") Long id
        ) {
            this.id = id;
        }

        public Long getId() {
            return id;
        }
    }
}

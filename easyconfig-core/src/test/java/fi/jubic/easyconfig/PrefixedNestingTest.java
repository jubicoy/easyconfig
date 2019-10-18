package fi.jubic.easyconfig;

import fi.jubic.easyconfig.annotations.EasyConfigProperty;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class PrefixedNestingTest {
    @Test
    public void testPrefixedNesting() throws MappingException {
        ParentConfig parent = new ConfigMapper(envProvider)
                .read(ParentConfig.class);

        assertThat(parent.getId(), is(111L));
        assertThat(parent.getChild().getId(), is(112L));
    }

    private static EnvProvider envProvider = new StaticEnvProvider() {{
        put("ID", "111");
        put("CHILD_ID", "112");
    }};

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

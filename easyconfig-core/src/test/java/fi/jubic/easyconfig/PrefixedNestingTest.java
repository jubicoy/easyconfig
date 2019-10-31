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

        assertThat(parent.id, is(111L));
        assertThat(parent.child.id, is(112L));
        assertThat(parent.child.grandChild.val, is("TEST VAL"));
    }

    private static EnvProvider envProvider = new StaticEnvProvider() {{
        put("ID", "111");
        put("CHILD_ID", "112");
        put("CHILD_GRAND_CHILD_VAL", "TEST VAL");
    }};

    static class ParentConfig {
        final Long id;
        final ChildConfig child;

        public ParentConfig(
                @EasyConfigProperty("ID") Long id,
                @EasyConfigProperty("CHILD_") ChildConfig child
        ) {
            this.id = id;
            this.child = child;
        }
    }

    static class ChildConfig {
        final Long id;
        final GrandChildConfig grandChild;

        public ChildConfig(
                @EasyConfigProperty("ID") Long id,
                @EasyConfigProperty("GRAND_CHILD_") GrandChildConfig grandChild
        ) {
            this.id = id;
            this.grandChild = grandChild;
        }
    }

    static class GrandChildConfig {
        final String val;

        public GrandChildConfig(
                @EasyConfigProperty("VAL") String val
        ) {
            this.val = val;
        }
    }
}

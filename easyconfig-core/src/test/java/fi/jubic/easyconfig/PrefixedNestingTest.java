package fi.jubic.easyconfig;

import fi.jubic.easyconfig.annotations.EasyConfigProperty;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PrefixedNestingTest {
    @Test
    void testPrefixedNesting() throws MappingException {
        ParentConfig parent = new ConfigMapper(envProvider)
                .read(ParentConfig.class);

        assertEquals(111L, parent.id);
        assertEquals(112L, parent.child.id);
        assertEquals("TEST VAL", parent.child.grandChild.val);
    }

    private static EnvProvider envProvider = new StaticEnvProvider()
            .with("ID", "111")
            .with("CHILD_ID", "112")
            .with("CHILD_GRAND_CHILD_VAL", "TEST VAL");

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

package fi.jubic.easyconfig.test;

import fi.jubic.easyconfig.ConfigMapper;
import fi.jubic.easyconfig.annotations.ConfigProperty;
import fi.jubic.easyconfig.providers.StaticEnvProvider;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PrefixedNestingTest {
    @Test
    void testPrefixedNesting() {
        ParentConfig parent = new ConfigMapper(
                new StaticEnvProvider()
                        .with("ID", "111")
                        .with("CHILD_ID", "112")
                        .with("CHILD_GRAND_CHILD_VAL", "TEST VAL")
                        .with("ROOT_VAL", "ROOT VAL")
        ).read(ParentConfig.class);

        assertEquals(111L, parent.id);
        assertEquals(112L, parent.child.id);
        assertEquals("TEST VAL", parent.child.grandChild.val);
        assertEquals("ROOT VAL", parent.child.grandChild.rootVal);
    }

    public static class ParentConfig {
        final Long id;
        final ChildConfig child;

        public ParentConfig(
                @ConfigProperty("ID") Long id,
                @ConfigProperty("CHILD_") ChildConfig child
        ) {
            this.id = id;
            this.child = child;
        }
    }

    public static class ChildConfig {
        final Long id;
        final GrandChildConfig grandChild;

        public ChildConfig(
                @ConfigProperty("ID") Long id,
                @ConfigProperty("GRAND_CHILD_") GrandChildConfig grandChild
        ) {
            this.id = id;
            this.grandChild = grandChild;
        }
    }

    public static class GrandChildConfig {
        final String val;
        final String rootVal;

        public GrandChildConfig(
                @ConfigProperty("VAL") String val,
                @ConfigProperty(value = "ROOT_VAL", noPrefix = true) String rootVal
        ) {
            this.val = val;
            this.rootVal = rootVal;
        }
    }
}

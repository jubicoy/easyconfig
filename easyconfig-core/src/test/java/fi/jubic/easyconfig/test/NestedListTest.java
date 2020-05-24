package fi.jubic.easyconfig.test;

import fi.jubic.easyconfig.ConfigMapper;
import fi.jubic.easyconfig.annotations.ConfigProperty;
import fi.jubic.easyconfig.providers.EnvProvider;
import fi.jubic.easyconfig.providers.StaticEnvProvider;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class NestedListTest {
    @Test
    void testNestedList() {
        EnvProvider envProvider = new StaticEnvProvider()
                .with("CHILD_0_ID", "1")
                .with("CHILD_0_ENABLED", "true")
                .with("CHILD_1_ID", "2")
                .with("CHILD_1_ENABLED", "false")
                .with("CHILD_3_ID", "3")
                .with("CHILD_3_ENABLED", "true")
                .with("EXTRA_PARAM_4", "extra");

        ParentTestConfig parent = new ConfigMapper(envProvider).read(ParentTestConfig.class);

        assertEquals(3, parent.testConfigs.size());

        assertEquals(1L, parent.testConfigs.get(0).id);
        assertTrue(parent.testConfigs.get(0).enabled);
        assertEquals(2L, parent.testConfigs.get(1).id);
        assertFalse(parent.testConfigs.get(1).enabled);
        assertEquals(3L, parent.testConfigs.get(2).id);
        assertTrue(parent.testConfigs.get(2).enabled);
    }

    @Test
    void testNestedListNestedInParentObject() {
        EnvProvider envProvider = new StaticEnvProvider()
                .with("PARENT_CHILD_0_ID", "1")
                .with("PARENT_CHILD_0_ENABLED", "true")
                .with("PARENT_CHILD_1_ID", "2")
                .with("PARENT_CHILD_1_ENABLED", "false")
                .with("PARENT_CHILD_3_ID", "3")
                .with("PARENT_CHILD_3_ENABLED", "true")
                .with("EXTRA_PARAM_4", "extra");

        NestingParentTestConfig nestingParent = new ConfigMapper(envProvider)
                .read(NestingParentTestConfig.class);

        assertEquals(3, nestingParent.parent.testConfigs.size());

        assertEquals(1L, nestingParent.parent.testConfigs.get(0).id);
        assertTrue(nestingParent.parent.testConfigs.get(0).enabled);
        assertEquals(2L, nestingParent.parent.testConfigs.get(1).id);
        assertFalse(nestingParent.parent.testConfigs.get(1).enabled);
        assertEquals(3L, nestingParent.parent.testConfigs.get(2).id);
        assertTrue(nestingParent.parent.testConfigs.get(2).enabled);
    }

    public static class NestingParentTestConfig {
        final ParentTestConfig parent;

        public NestingParentTestConfig(
                @ConfigProperty("PARENT_") ParentTestConfig parent
        ) {
            this.parent = parent;
        }
    }

    public static class ParentTestConfig {
        final List<TestConfig> testConfigs;

        public ParentTestConfig(
                @ConfigProperty("CHILD_{}_") List<TestConfig> testConfigs
        ) {
            this.testConfigs = testConfigs;
        }
    }

    public static class TestConfig {
        final long id;
        final boolean enabled;

        public TestConfig(
                @ConfigProperty("ID") long id,
                @ConfigProperty("ENABLED") boolean enabled
        ) {
            this.id = id;
            this.enabled = enabled;
        }
    }
}

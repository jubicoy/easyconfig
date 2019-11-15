package fi.jubic.easyconfig;

import fi.jubic.easyconfig.annotations.EasyConfigProperty;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class NestedListTest {
    @Test
    void testNestedList() throws MappingException {
        EnvProvider envProvider = new StaticEnvProvider() {{
            put("CHILD_0_ID", "1");
            put("CHILD_0_ENABLED", "true");
            put("CHILD_1_ID", "2");
            put("CHILD_1_ENABLED", "false");
            put("CHILD_3_ID", "3");
            put("CHILD_3_ENABLED", "true");
            put("EXTRA_PARAM_4", "extra");
        }};

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
    void testNestedListNestedInParentObject() throws MappingException {
        EnvProvider envProvider = new StaticEnvProvider() {{
            put("PARENT_CHILD_0_ID", "1");
            put("PARENT_CHILD_0_ENABLED", "true");
            put("PARENT_CHILD_1_ID", "2");
            put("PARENT_CHILD_1_ENABLED", "false");
            put("PARENT_CHILD_3_ID", "3");
            put("PARENT_CHILD_3_ENABLED", "true");
            put("EXTRA_PARAM_4", "extra");
        }};

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

    static class NestingParentTestConfig {
        final ParentTestConfig parent;

        public NestingParentTestConfig(
                @EasyConfigProperty("PARENT_") ParentTestConfig parent
        ) {
            this.parent = parent;
        }
    }

    static class ParentTestConfig {
        final List<TestConfig> testConfigs;

        public ParentTestConfig(
                @EasyConfigProperty("CHILD_{}_") List<TestConfig> testConfigs
        ) {
            this.testConfigs = testConfigs;
        }
    }

    static class TestConfig {
        final long id;
        final boolean enabled;

        public TestConfig(
                @EasyConfigProperty("ID") long id,
                @EasyConfigProperty("ENABLED") boolean enabled
        ) {
            this.id = id;
            this.enabled = enabled;
        }
    }
}

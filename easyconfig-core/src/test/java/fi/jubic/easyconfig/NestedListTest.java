package fi.jubic.easyconfig;

import fi.jubic.easyconfig.annotations.EasyConfigProperty;

import java.util.List;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class NestedListTest {
    @Test
    public void testNestedList() throws MappingException {
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

        assertThat(parent.testConfigs.size(), is(3));

        assertThat(parent.testConfigs.get(0).id, is(1L));
        assertThat(parent.testConfigs.get(0).enabled, is(true));
        assertThat(parent.testConfigs.get(1).id, is(2L));
        assertThat(parent.testConfigs.get(1).enabled, is(false));
        assertThat(parent.testConfigs.get(2).id, is(3L));
        assertThat(parent.testConfigs.get(2).enabled, is(true));
    }

    @Test
    public void testNestedListNestedInParentObject() throws MappingException {
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

        assertThat(nestingParent.parent.testConfigs.size(), is(3));

        assertThat(nestingParent.parent.testConfigs.get(0).id, is(1L));
        assertThat(nestingParent.parent.testConfigs.get(0).enabled, is(true));
        assertThat(nestingParent.parent.testConfigs.get(1).id, is(2L));
        assertThat(nestingParent.parent.testConfigs.get(1).enabled, is(false));
        assertThat(nestingParent.parent.testConfigs.get(2).id, is(3L));
        assertThat(nestingParent.parent.testConfigs.get(2).enabled, is(true));
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

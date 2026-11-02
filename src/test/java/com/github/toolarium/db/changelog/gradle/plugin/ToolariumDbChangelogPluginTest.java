/*
 * ToolariumDbChangelogPluginTest.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.db.changelog.gradle.plugin;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.github.toolarium.db.changelog.gradle.plugin.task.ChangelogClearChecksumsTask;
import com.github.toolarium.db.changelog.gradle.plugin.task.ChangelogDropAllTask;
import com.github.toolarium.db.changelog.gradle.plugin.task.ChangelogGenerateRollbackSqlTask;
import com.github.toolarium.db.changelog.gradle.plugin.task.ChangelogGenerateSqlTask;
import com.github.toolarium.db.changelog.gradle.plugin.task.ChangelogHistoryTask;
import com.github.toolarium.db.changelog.gradle.plugin.task.ChangelogRollbackTask;
import com.github.toolarium.db.changelog.gradle.plugin.task.ChangelogStatusTask;
import com.github.toolarium.db.changelog.gradle.plugin.task.ChangelogSyncTask;
import com.github.toolarium.db.changelog.gradle.plugin.task.ChangelogTagTask;
import com.github.toolarium.db.changelog.gradle.plugin.task.ChangelogUpdateTask;
import com.github.toolarium.db.changelog.gradle.plugin.task.ChangelogValidateTask;
import java.util.Arrays;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


/**
 * Tests for {@link ToolariumDbChangelogPlugin}.
 *
 * @author patrick
 */
public class ToolariumDbChangelogPluginTest {

    private static final String TASK_UPDATE = "changelogUpdate";
    private static final String TASK_ROLLBACK = "changelogRollback";
    private static final String TASK_STATUS = "changelogStatus";
    private static final String TASK_VALIDATE = "changelogValidate";
    private static final String TASK_GENERATE_SQL = "changelogGenerateSql";
    private static final String TASK_SYNC = "changelogSync";
    private static final String TASK_TAG = "changelogTag";
    private static final String TASK_HISTORY = "changelogHistory";
    private static final String TASK_CLEAR_CHECKSUMS = "changelogClearChecksums";
    private static final String TASK_GENERATE_ROLLBACK_SQL = "changelogGenerateRollbackSql";
    private static final String TASK_DROP_ALL = "changelogDropAll";

    private Project project;


    /**
     * Sets up a test project with the plugin applied.
     */
    @BeforeEach
    public void setUp() {
        project = ProjectBuilder.builder().build();
        project.getPlugins().apply("com.github.toolarium.db-changelog");
    }


    /**
     * Tests that the plugin registers the dbChangelog extension.
     */
    @Test
    public void testExtensionRegistered() {
        DbChangelogExtension extension = project.getExtensions()
                .findByType(DbChangelogExtension.class);
        assertNotNull(extension, "dbChangelog extension should be registered");
    }


    /**
     * Tests that the extension has proper default values.
     */
    @Test
    public void testExtensionDefaults() {
        DbChangelogExtension extension = project.getExtensions()
                .findByType(DbChangelogExtension.class);
        assertNotNull(extension);

        assertEquals("src/main/resources/db/changelog/changelog-master.db.yaml",
                extension.getChangelog().get());
        assertEquals("DB_CHANGELOG", extension.getChangelogTableName().get());
        assertEquals(false, extension.getLeaderElection().get());
        assertTrue(extension.getContexts().get().isEmpty());
        assertTrue(extension.getLabels().get().isEmpty());
        assertFalse(extension.getUrl().isPresent());
        assertFalse(extension.getUsername().isPresent());
        assertFalse(extension.getPassword().isPresent());
        assertFalse(extension.getDriver().isPresent());
        assertFalse(extension.getDefaultSchemaName().isPresent());
        assertFalse(extension.getRollbackCount().isPresent());
        assertFalse(extension.getRollbackTag().isPresent());
        assertFalse(extension.getRollbackDate().isPresent());
    }


    /**
     * Tests that all changelog tasks are registered.
     */
    @Test
    public void testTasksRegistered() {
        assertTaskRegistered(TASK_UPDATE, ChangelogUpdateTask.class);
        assertTaskRegistered(TASK_ROLLBACK, ChangelogRollbackTask.class);
        assertTaskRegistered(TASK_STATUS, ChangelogStatusTask.class);
        assertTaskRegistered(TASK_VALIDATE, ChangelogValidateTask.class);
        assertTaskRegistered(TASK_GENERATE_SQL, ChangelogGenerateSqlTask.class);
        assertTaskRegistered(TASK_SYNC, ChangelogSyncTask.class);
        assertTaskRegistered(TASK_TAG, ChangelogTagTask.class);
        assertTaskRegistered(TASK_HISTORY, ChangelogHistoryTask.class);
        assertTaskRegistered(TASK_CLEAR_CHECKSUMS, ChangelogClearChecksumsTask.class);
        assertTaskRegistered(TASK_GENERATE_ROLLBACK_SQL, ChangelogGenerateRollbackSqlTask.class);
        assertTaskRegistered(TASK_DROP_ALL, ChangelogDropAllTask.class);
    }


    /**
     * Tests that all changelog tasks belong to the db-changelog group.
     */
    @Test
    public void testTaskGroup() {
        String[] taskNames = {
            TASK_UPDATE, TASK_ROLLBACK,
            TASK_STATUS, TASK_VALIDATE, TASK_GENERATE_SQL,
            TASK_SYNC, TASK_TAG, TASK_HISTORY,
            TASK_CLEAR_CHECKSUMS, TASK_GENERATE_ROLLBACK_SQL,
            TASK_DROP_ALL
        };
        for (String taskName : taskNames) {
            Task task = project.getTasks().getByName(taskName);
            assertEquals(ToolariumDbChangelogPlugin.TASK_GROUP, task.getGroup(),
                    taskName + " should belong to the db-changelog group");
        }
    }


    /**
     * Tests that all changelog tasks have a description set.
     */
    @Test
    public void testTaskDescriptions() {
        String[] taskNames = {
            TASK_UPDATE, TASK_ROLLBACK,
            TASK_STATUS, TASK_VALIDATE, TASK_GENERATE_SQL,
            TASK_SYNC, TASK_TAG, TASK_HISTORY,
            TASK_CLEAR_CHECKSUMS, TASK_GENERATE_ROLLBACK_SQL,
            TASK_DROP_ALL
        };
        for (String taskName : taskNames) {
            Task task = project.getTasks().getByName(taskName);
            assertNotNull(task.getDescription(),
                    taskName + " should have a description");
        }
    }


    /**
     * Tests that extension properties are wired to task properties for basic fields.
     */
    @Test
    public void testExtensionWiringBasic() {
        DbChangelogExtension extension = project.getExtensions()
                .findByType(DbChangelogExtension.class);
        assertNotNull(extension);

        extension.getUrl().set("jdbc:h2:mem:test");
        extension.getUsername().set("sa");
        extension.getChangelog().set("custom/path.db.yaml");

        ChangelogUpdateTask updateTask = (ChangelogUpdateTask) project.getTasks()
                .getByName(TASK_UPDATE);
        assertEquals("jdbc:h2:mem:test", updateTask.getUrl().get());
        assertEquals("sa", updateTask.getUsername().get());
        assertEquals("custom/path.db.yaml", updateTask.getChangelog().get());
    }


    /**
     * Tests that extension password and driver properties are wired to task properties.
     */
    @Test
    public void testExtensionWiringPasswordAndDriver() {
        DbChangelogExtension extension = project.getExtensions()
                .findByType(DbChangelogExtension.class);
        assertNotNull(extension);

        extension.getPassword().set("secret");
        extension.getDriver().set("org.h2.Driver");

        ChangelogUpdateTask updateTask = (ChangelogUpdateTask) project.getTasks()
                .getByName(TASK_UPDATE);
        assertEquals("secret", updateTask.getPassword().get());
        assertEquals("org.h2.Driver", updateTask.getDriver().get());
    }


    /**
     * Tests that extension contexts and labels are wired to task properties.
     */
    @Test
    public void testExtensionWiringContextsAndLabels() {
        DbChangelogExtension extension = project.getExtensions()
                .findByType(DbChangelogExtension.class);
        assertNotNull(extension);

        extension.getContexts().set(Arrays.asList("prod", "shared"));
        extension.getLabels().set(Arrays.asList("v1.0", "core"));

        ChangelogValidateTask validateTask = (ChangelogValidateTask) project.getTasks()
                .getByName(TASK_VALIDATE);
        assertEquals(Arrays.asList("prod", "shared"), validateTask.getContexts().get());
        assertEquals(Arrays.asList("v1.0", "core"), validateTask.getLabels().get());
    }


    /**
     * Tests that extension changelogTableName and defaultSchemaName are wired to task properties.
     */
    @Test
    public void testExtensionWiringTableAndSchema() {
        DbChangelogExtension extension = project.getExtensions()
                .findByType(DbChangelogExtension.class);
        assertNotNull(extension);

        extension.getChangelogTableName().set("CUSTOM_CHANGELOG");
        extension.getDefaultSchemaName().set("myschema");

        ChangelogSyncTask syncTask = (ChangelogSyncTask) project.getTasks()
                .getByName(TASK_SYNC);
        assertEquals("CUSTOM_CHANGELOG", syncTask.getChangelogTableName().get());
        assertEquals("myschema", syncTask.getDefaultSchemaName().get());
    }


    /**
     * Tests that extension leaderElection property is wired to task properties.
     */
    @Test
    public void testExtensionWiringLeaderElection() {
        DbChangelogExtension extension = project.getExtensions()
                .findByType(DbChangelogExtension.class);
        assertNotNull(extension);

        extension.getLeaderElection().set(true);

        ChangelogUpdateTask updateTask = (ChangelogUpdateTask) project.getTasks()
                .getByName(TASK_UPDATE);
        assertEquals(true, updateTask.getLeaderElection().get());
    }


    /**
     * Tests that extension wiring works across all task types, not just update.
     */
    @Test
    public void testExtensionWiringAcrossAllTasks() {
        DbChangelogExtension extension = project.getExtensions()
                .findByType(DbChangelogExtension.class);
        assertNotNull(extension);

        extension.getUrl().set("jdbc:h2:mem:alltest");

        String[] taskNames = {
            TASK_UPDATE, TASK_ROLLBACK,
            TASK_STATUS, TASK_VALIDATE, TASK_GENERATE_SQL,
            TASK_SYNC, TASK_TAG, TASK_HISTORY,
            TASK_CLEAR_CHECKSUMS, TASK_GENERATE_ROLLBACK_SQL,
            TASK_DROP_ALL
        };
        for (String taskName : taskNames) {
            AbstractDbChangelogTask task = (AbstractDbChangelogTask) project.getTasks()
                    .getByName(taskName);
            assertEquals("jdbc:h2:mem:alltest", task.getUrl().get(),
                    taskName + " should have the URL from the extension");
        }
    }


    /**
     * Tests that rollback extension properties can be set.
     */
    @Test
    public void testExtensionRollbackProperties() {
        DbChangelogExtension extension = project.getExtensions()
                .findByType(DbChangelogExtension.class);
        assertNotNull(extension);

        extension.getRollbackCount().set(5);
        assertEquals(5, extension.getRollbackCount().get());

        extension.getRollbackTag().set("v1.0");
        assertEquals("v1.0", extension.getRollbackTag().get());

        extension.getRollbackDate().set("2026-01-15T10:00:00Z");
        assertEquals("2026-01-15T10:00:00Z", extension.getRollbackDate().get());
    }


    /**
     * Asserts that a task with the given name is registered and has the expected type.
     *
     * @param name the task name
     * @param expectedType the expected task class
     */
    private void assertTaskRegistered(String name, Class<? extends Task> expectedType) {
        Task task = project.getTasks().findByName(name);
        assertNotNull(task, "Task '" + name + "' should be registered");
        assertTrue(expectedType.isInstance(task),
                "Task '" + name + "' should be of type " + expectedType.getSimpleName());
    }
}

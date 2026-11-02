/*
 * ChangelogRollbackTaskTest.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.db.changelog.gradle.plugin.task;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.gradle.api.GradleException;
import org.gradle.api.Project;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


/**
 * Tests for {@link ChangelogRollbackTask}.
 *
 * @author patrick
 */
public class ChangelogRollbackTaskTest {

    private Project project;
    private ChangelogRollbackTask rollbackTask;


    /**
     * Sets up a test project with the plugin applied.
     */
    @BeforeEach
    public void setUp() {
        project = ProjectBuilder.builder().build();
        project.getPlugins().apply("com.github.toolarium.db-changelog");
        rollbackTask = (ChangelogRollbackTask) project.getTasks().getByName("changelogRollback");
    }


    /**
     * Tests that rollback fails when no rollback parameter is specified.
     */
    @Test
    public void testRollbackFailsWithNoParameters() {
        rollbackTask.getUrl().set("jdbc:h2:mem:test");

        GradleException exception = assertThrows(GradleException.class, () -> {
            rollbackTask.executeRollback();
        });
        assertNotNull(exception.getMessage());
        assertTrue(exception.getMessage().contains("rollbackCount"),
                "Error should mention rollbackCount: " + exception.getMessage());
    }


    /**
     * Tests that rollback fails when multiple rollback parameters are specified.
     */
    @Test
    public void testRollbackFailsWithMultipleParameters() {
        rollbackTask.getUrl().set("jdbc:h2:mem:test");
        rollbackTask.getRollbackCount().set(3);
        rollbackTask.getRollbackTag().set("v1.0");

        GradleException exception = assertThrows(GradleException.class, () -> {
            rollbackTask.executeRollback();
        });
        assertNotNull(exception.getMessage());
        assertTrue(exception.getMessage().contains("Only one"),
                "Error should mention only one parameter: " + exception.getMessage());
    }


    /**
     * Tests that rollback task properties for count, tag, and date default to not present.
     */
    @Test
    public void testRollbackPropertiesDefaultToNotPresent() {
        assertFalse(rollbackTask.getRollbackCount().isPresent());
        assertFalse(rollbackTask.getRollbackTag().isPresent());
        assertFalse(rollbackTask.getRollbackDate().isPresent());
    }


    /**
     * Tests that rollback fails when all three parameters are specified.
     */
    @Test
    public void testRollbackFailsWithAllThreeParameters() {
        rollbackTask.getUrl().set("jdbc:h2:mem:test");
        rollbackTask.getRollbackCount().set(3);
        rollbackTask.getRollbackTag().set("v1.0");
        rollbackTask.getRollbackDate().set("2026-01-15T10:00:00Z");

        GradleException exception = assertThrows(GradleException.class, () -> {
            rollbackTask.executeRollback();
        });
        assertNotNull(exception.getMessage());
        assertTrue(exception.getMessage().contains("Only one"),
                "Error should mention only one parameter: " + exception.getMessage());
    }
}

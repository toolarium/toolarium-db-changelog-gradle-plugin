/*
 * AbstractDbChangelogTaskTest.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.db.changelog.gradle.plugin;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.github.toolarium.db.changelog.gradle.plugin.task.ChangelogUpdateTask;
import org.gradle.api.GradleException;
import org.gradle.api.Project;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


/**
 * Tests for {@link AbstractDbChangelogTask}.
 *
 * @author patrick
 */
public class AbstractDbChangelogTaskTest {

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
     * Tests that createRunner fails when URL is not configured.
     */
    @Test
    public void testCreateRunnerFailsWithoutUrl() {
        ChangelogUpdateTask task = (ChangelogUpdateTask) project.getTasks()
                .getByName("changelogUpdate");

        GradleException exception = assertThrows(GradleException.class, () -> {
            task.executeUpdate();
        });
        assertNotNull(exception.getMessage());
        assertTrue(exception.getMessage().contains("URL"),
                "Error message should mention URL: " + exception.getMessage());
    }


    /**
     * Tests that createRunner fails when changelog path is not configured
     * but URL is set.
     */
    @Test
    public void testCreateRunnerFailsWithoutChangelog() {
        DbChangelogExtension extension = project.getExtensions()
                .findByType(DbChangelogExtension.class);
        assertNotNull(extension);

        extension.getUrl().set("jdbc:h2:mem:test");
        // Clear the default changelog path
        extension.getChangelog().set((String) null);

        ChangelogUpdateTask task = (ChangelogUpdateTask) project.getTasks()
                .getByName("changelogUpdate");

        GradleException exception = assertThrows(GradleException.class, () -> {
            task.executeUpdate();
        });
        assertNotNull(exception.getMessage());
        assertTrue(exception.getMessage().contains("hangelog"),
                "Error message should mention changelog: " + exception.getMessage());
    }


    /**
     * Tests that createRunner fails when an invalid JDBC driver class is configured.
     */
    @Test
    public void testCreateRunnerFailsWithInvalidDriver() {
        DbChangelogExtension extension = project.getExtensions()
                .findByType(DbChangelogExtension.class);
        assertNotNull(extension);

        extension.getUrl().set("jdbc:h2:mem:test");
        extension.getDriver().set("com.nonexistent.Driver");

        ChangelogUpdateTask task = (ChangelogUpdateTask) project.getTasks()
                .getByName("changelogUpdate");

        GradleException exception = assertThrows(GradleException.class, () -> {
            task.executeUpdate();
        });
        assertNotNull(exception.getMessage());
        assertTrue(exception.getMessage().contains("com.nonexistent.Driver"),
                "Error message should mention driver class: " + exception.getMessage());
    }
}

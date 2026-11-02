/*
 * ChangelogValidateTask.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.db.changelog.gradle.plugin.task;

import com.github.toolarium.db.changelog.IChangelogRunner;
import com.github.toolarium.db.changelog.gradle.plugin.AbstractDbChangelogTask;
import org.gradle.api.GradleException;
import org.gradle.api.tasks.TaskAction;


/**
 * Gradle task that validates the changelog for errors and checksum mismatches.
 *
 * @author patrick
 */
public class ChangelogValidateTask extends AbstractDbChangelogTask {

    /**
     * Executes the changelog validation.
     *
     * @throws GradleException if the validation fails
     */
    @TaskAction
    public void executeValidate() {
        getLogger().lifecycle("Validating changelog...");

        try {
            IChangelogRunner runner = createRunner();
            runner.validate();
            getLogger().lifecycle("Changelog validation passed.");
        } catch (GradleException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new GradleException("Changelog validation failed: " + ex.getMessage(), ex);
        }
    }
}

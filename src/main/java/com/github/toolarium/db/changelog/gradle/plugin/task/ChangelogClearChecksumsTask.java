/*
 * ChangelogClearChecksumsTask.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.db.changelog.gradle.plugin.task;

import com.github.toolarium.db.changelog.IChangelogRunner;
import com.github.toolarium.db.changelog.gradle.plugin.AbstractDbChangelogTask;
import org.gradle.api.GradleException;
import org.gradle.api.tasks.TaskAction;


/**
 * Gradle task that clears all stored checksums, forcing recalculation on the next run.
 *
 * @author patrick
 */
public class ChangelogClearChecksumsTask extends AbstractDbChangelogTask {

    /**
     * Executes the clear checksums operation.
     *
     * @throws GradleException if the operation fails
     */
    @TaskAction
    public void executeClearChecksums() {
        getLogger().lifecycle("Clearing all stored checksums...");

        try {
            IChangelogRunner runner = createRunner();
            runner.clearChecksums();
            getLogger().lifecycle("Checksums cleared successfully.");
        } catch (GradleException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new GradleException("Clearing checksums failed: " + ex.getMessage(), ex);
        }
    }
}

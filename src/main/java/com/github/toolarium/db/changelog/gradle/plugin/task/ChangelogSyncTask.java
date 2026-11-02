/*
 * ChangelogSyncTask.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.db.changelog.gradle.plugin.task;

import com.github.toolarium.db.changelog.IChangelogRunner;
import com.github.toolarium.db.changelog.gradle.plugin.AbstractDbChangelogTask;
import org.gradle.api.GradleException;
import org.gradle.api.tasks.TaskAction;


/**
 * Gradle task that marks all pending changesets as executed without running them (baselining).
 *
 * @author patrick
 */
public class ChangelogSyncTask extends AbstractDbChangelogTask {

    /**
     * Executes the changelog sync.
     *
     * @throws GradleException if the sync fails
     */
    @TaskAction
    public void executeSync() {
        getLogger().lifecycle("Syncing changelog (marking all pending changesets as executed)...");

        try {
            IChangelogRunner runner = createRunner();
            runner.sync();
            getLogger().lifecycle("Changelog sync completed successfully.");
        } catch (GradleException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new GradleException("Changelog sync failed: " + ex.getMessage(), ex);
        }
    }
}

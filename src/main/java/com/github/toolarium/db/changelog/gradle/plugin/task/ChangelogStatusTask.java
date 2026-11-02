/*
 * ChangelogStatusTask.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.db.changelog.gradle.plugin.task;

import com.github.toolarium.db.changelog.IChangelogRunner;
import com.github.toolarium.db.changelog.gradle.plugin.AbstractDbChangelogTask;
import com.github.toolarium.db.changelog.model.IChangelogChangeSetStatus;
import java.util.List;
import org.gradle.api.GradleException;
import org.gradle.api.tasks.TaskAction;


/**
 * Gradle task that displays the status of pending and applied changesets.
 *
 * @author patrick
 */
public class ChangelogStatusTask extends AbstractDbChangelogTask {

    /**
     * Executes the changelog status check.
     *
     * @throws GradleException if the status check fails
     */
    @TaskAction
    public void executeStatus() {
        try {
            IChangelogRunner runner = createRunner();
            List<IChangelogChangeSetStatus> statusList = runner.status();

            if (statusList.isEmpty()) {
                getLogger().lifecycle("No pending changesets.");
            } else {
                getLogger().lifecycle(statusList.size() + " pending changeset(s):");
                for (IChangelogChangeSetStatus status : statusList) {
                    getLogger().lifecycle("  - " + status.toString());
                }
            }
        } catch (GradleException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new GradleException("Changelog status check failed: " + ex.getMessage(), ex);
        }
    }
}

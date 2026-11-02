/*
 * ChangelogHistoryTask.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.db.changelog.gradle.plugin.task;

import com.github.toolarium.db.changelog.IChangelogRunner;
import com.github.toolarium.db.changelog.gradle.plugin.AbstractDbChangelogTask;
import com.github.toolarium.db.changelog.model.IChangelogTrackingEntry;
import java.util.List;
import org.gradle.api.GradleException;
import org.gradle.api.tasks.TaskAction;


/**
 * Gradle task that displays the history of applied changesets.
 *
 * @author patrick
 */
public class ChangelogHistoryTask extends AbstractDbChangelogTask {

    /**
     * Executes the changelog history display.
     *
     * @throws GradleException if the history query fails
     */
    @TaskAction
    public void executeHistory() {
        try {
            IChangelogRunner runner = createRunner();
            List<IChangelogTrackingEntry> historyList = runner.history();

            if (historyList.isEmpty()) {
                getLogger().lifecycle("No applied changesets.");
            } else {
                getLogger().lifecycle(historyList.size() + " applied changeset(s):");
                for (IChangelogTrackingEntry entry : historyList) {
                    getLogger().lifecycle("  - " + entry.toString());
                }
            }
        } catch (GradleException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new GradleException("Changelog history query failed: " + ex.getMessage(), ex);
        }
    }
}

/*
 * ChangelogDropAllTask.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.db.changelog.gradle.plugin.task;

//import com.github.toolarium.db.changelog.IChangelogRunner;
import com.github.toolarium.db.changelog.gradle.plugin.AbstractDbChangelogTask;
import org.gradle.api.GradleException;
import org.gradle.api.tasks.TaskAction;


/**
 * Gradle task that drops all database objects managed by the changelog.
 * This task is intended for development and testing environments only.
 *
 * @author patrick
 */
public class ChangelogDropAllTask extends AbstractDbChangelogTask {

    /**
     * Executes the drop-all operation.
     *
     * @throws GradleException if the drop-all operation fails
     */
    @TaskAction
    public void executeDropAll() {
        getLogger().lifecycle("Dropping all database objects...");

        // TODO: uncomment when dropAll() is available in IChangelogRunner
        //try {
        //    IChangelogRunner runner = createRunner();
        //    runner.dropAll();
        //    getLogger().lifecycle("All database objects dropped successfully.");
        //} catch (Exception ex) {
        //    throw new GradleException("Drop-all failed: " + ex.getMessage(), ex);
        //}
        throw new GradleException("The dropAll operation is not yet available in IChangelogRunner.");
    }
}

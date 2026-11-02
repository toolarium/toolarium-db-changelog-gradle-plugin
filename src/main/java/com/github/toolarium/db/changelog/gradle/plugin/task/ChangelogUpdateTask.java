/*
 * ChangelogUpdateTask.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.db.changelog.gradle.plugin.task;

import com.github.toolarium.db.changelog.IChangelogRunner;
import com.github.toolarium.db.changelog.gradle.plugin.AbstractDbChangelogTask;
import org.gradle.api.GradleException;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.Optional;
import org.gradle.api.tasks.TaskAction;


/**
 * Gradle task that applies pending changesets to the database.
 * If {@code updateCount} is specified, only that many changesets are applied.
 *
 * @author patrick
 */
public class ChangelogUpdateTask extends AbstractDbChangelogTask {

    private final Property<Integer> updateCount;


    /**
     * Constructor for ChangelogUpdateTask.
     *
     * @throws GradleException if a project property has an invalid value
     */
    public ChangelogUpdateTask() {
        updateCount = getProject().getObjects().property(Integer.class);

        if (getProject().hasProperty("updateCount")) {
            String value = getProject().property("updateCount").toString();
            try {
                updateCount.set(Integer.parseInt(value));
            } catch (NumberFormatException e) {
                throw new GradleException("Invalid updateCount value '" + value + "': must be an integer.", e);
            }
        }
    }


    /**
     * Gets the maximum number of changesets to apply.
     *
     * @return the updateCount property
     */
    @Input
    @Optional
    public Property<Integer> getUpdateCount() {
        return updateCount;
    }


    /**
     * Executes the changelog update.
     *
     * @throws GradleException if the update fails
     */
    @TaskAction
    public void executeUpdate() {
        try {
            IChangelogRunner runner = createRunner();

            if (updateCount.isPresent()) {
                int count = updateCount.get();
                getLogger().lifecycle("Applying up to " + count + " pending changeset(s)...");
                runner.update(count);
            } else {
                getLogger().lifecycle("Applying all pending changesets...");
                runner.update();
            }

            getLogger().lifecycle("Changelog update completed successfully.");
        } catch (GradleException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new GradleException("Changelog update failed: " + ex.getMessage(), ex);
        }
    }
}

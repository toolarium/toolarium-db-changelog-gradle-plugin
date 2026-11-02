/*
 * ChangelogRollbackTask.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.db.changelog.gradle.plugin.task;

import com.github.toolarium.db.changelog.IChangelogRunner;
import com.github.toolarium.db.changelog.gradle.plugin.AbstractDbChangelogTask;
import java.time.Instant;
import java.time.format.DateTimeParseException;
import org.gradle.api.GradleException;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.Optional;
import org.gradle.api.tasks.TaskAction;


/**
 * Gradle task that rolls back changesets by count, tag, or date.
 *
 * <p>Exactly one of {@code rollbackCount}, {@code rollbackTag}, or {@code rollbackDate}
 * must be specified. The value can be set via the {@code dbChangelog} extension or
 * as a project property (e.g., {@code -ProllbackCount=3}).
 *
 * @author patrick
 */
public class ChangelogRollbackTask extends AbstractDbChangelogTask {

    private final Property<Integer> rollbackCount;
    private final Property<String> rollbackTag;
    private final Property<String> rollbackDate;


    /**
     * Constructor for ChangelogRollbackTask.
     *
     * @throws GradleException if a project property has an invalid value
     */
    public ChangelogRollbackTask() {
        rollbackCount = getProject().getObjects().property(Integer.class);
        rollbackTag = getProject().getObjects().property(String.class);
        rollbackDate = getProject().getObjects().property(String.class);

        if (getProject().hasProperty("rollbackCount")) {
            String value = getProject().property("rollbackCount").toString();
            try {
                rollbackCount.set(Integer.parseInt(value));
            } catch (NumberFormatException e) {
                throw new GradleException("Invalid rollbackCount value '" + value + "': must be an integer.", e);
            }
        }
        if (getProject().hasProperty("rollbackTag")) {
            rollbackTag.set(getProject().property("rollbackTag").toString());
        }
        if (getProject().hasProperty("rollbackDate")) {
            rollbackDate.set(getProject().property("rollbackDate").toString());
        }
    }


    /**
     * Gets the number of changesets to roll back.
     *
     * @return the rollbackCount property
     */
    @Input
    @Optional
    public Property<Integer> getRollbackCount() {
        return rollbackCount;
    }


    /**
     * Gets the tag to roll back to.
     *
     * @return the rollbackTag property
     */
    @Input
    @Optional
    public Property<String> getRollbackTag() {
        return rollbackTag;
    }


    /**
     * Gets the date to roll back to (ISO-8601 format).
     *
     * @return the rollbackDate property
     */
    @Input
    @Optional
    public Property<String> getRollbackDate() {
        return rollbackDate;
    }


    /**
     * Executes the changelog rollback.
     *
     * @throws GradleException if the rollback fails or parameters are invalid
     */
    @TaskAction
    public void executeRollback() {
        int specified = 0;
        if (rollbackCount.isPresent()) {
            specified++;
        }
        if (rollbackTag.isPresent()) {
            specified++;
        }
        if (rollbackDate.isPresent()) {
            specified++;
        }

        if (specified == 0) {
            throw new GradleException(
                    "One of rollbackCount, rollbackTag, or rollbackDate must be specified. "
                    + "Use -ProllbackCount=N, -ProllbackTag=TAG, or -ProllbackDate=ISO_DATE.");
        }
        if (specified > 1) {
            throw new GradleException(
                    "Only one of rollbackCount, rollbackTag, or rollbackDate may be specified.");
        }

        try {
            IChangelogRunner runner = createRunner();

            if (rollbackCount.isPresent()) {
                int count = rollbackCount.get();
                getLogger().lifecycle("Rolling back " + count + " changeset(s)...");
                runner.rollback(count);
            } else if (rollbackTag.isPresent()) {
                String tag = rollbackTag.get();
                getLogger().lifecycle("Rolling back to tag '" + tag + "'...");
                runner.rollback(tag);
            } else {
                String dateStr = rollbackDate.get();
                getLogger().lifecycle("Rolling back to date '" + dateStr + "'...");
                try {
                    runner.rollback(Instant.parse(dateStr));
                } catch (DateTimeParseException e) {
                    throw new GradleException(
                            "Invalid rollbackDate '" + dateStr + "': must be ISO-8601 format (e.g., 2026-01-15T10:00:00Z).", e);
                }
            }

            getLogger().lifecycle("Changelog rollback completed successfully.");
        } catch (GradleException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new GradleException("Changelog rollback failed: " + ex.getMessage(), ex);
        }
    }
}

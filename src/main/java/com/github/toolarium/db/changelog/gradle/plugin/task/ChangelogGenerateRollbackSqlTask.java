/*
 * ChangelogGenerateRollbackSqlTask.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.db.changelog.gradle.plugin.task;

import com.github.toolarium.db.changelog.IChangelogRunner;
import com.github.toolarium.db.changelog.gradle.plugin.AbstractDbChangelogTask;
import java.util.List;
import org.gradle.api.GradleException;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.Optional;
import org.gradle.api.tasks.TaskAction;


/**
 * Gradle task that generates the rollback SQL for the last N changesets without executing.
 *
 * @author patrick
 */
public class ChangelogGenerateRollbackSqlTask extends AbstractDbChangelogTask {

    private final Property<Integer> rollbackCount;


    /**
     * Constructor for ChangelogGenerateRollbackSqlTask.
     *
     * @throws GradleException if a project property has an invalid value
     */
    public ChangelogGenerateRollbackSqlTask() {
        rollbackCount = getProject().getObjects().property(Integer.class);

        if (getProject().hasProperty("rollbackCount")) {
            String value = getProject().property("rollbackCount").toString();
            try {
                rollbackCount.set(Integer.parseInt(value));
            } catch (NumberFormatException e) {
                throw new GradleException("Invalid rollbackCount value '" + value + "': must be an integer.", e);
            }
        }
    }


    /**
     * Gets the number of changesets to generate rollback SQL for.
     *
     * @return the rollbackCount property
     */
    @Input
    @Optional
    public Property<Integer> getRollbackCount() {
        return rollbackCount;
    }


    /**
     * Executes the rollback SQL generation.
     *
     * @throws GradleException if the generation fails or the count is missing
     */
    @TaskAction
    public void executeGenerateRollbackSql() {
        if (!rollbackCount.isPresent()) {
            throw new GradleException("rollbackCount is required. Use -ProllbackCount=N.");
        }

        int count = rollbackCount.get();
        getLogger().lifecycle("Generating rollback SQL for the last " + count + " changeset(s)...");

        try {
            IChangelogRunner runner = createRunner();
            List<String> sqlStatements = runner.generateRollbackSql(count);

            if (sqlStatements.isEmpty()) {
                getLogger().lifecycle("No rollback SQL generated.");
            } else {
                getLogger().lifecycle("Generated " + sqlStatements.size() + " rollback SQL statement(s):");
                for (String sql : sqlStatements) {
                    getLogger().lifecycle(sql);
                }
            }
        } catch (GradleException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new GradleException("Rollback SQL generation failed: " + ex.getMessage(), ex);
        }
    }
}

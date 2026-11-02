/*
 * ToolariumDbChangelogPlugin.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.db.changelog.gradle.plugin;

import com.github.toolarium.db.changelog.gradle.plugin.task.ChangelogClearChecksumsTask;
import com.github.toolarium.db.changelog.gradle.plugin.task.ChangelogDropAllTask;
import com.github.toolarium.db.changelog.gradle.plugin.task.ChangelogGenerateRollbackSqlTask;
import com.github.toolarium.db.changelog.gradle.plugin.task.ChangelogGenerateSqlTask;
import com.github.toolarium.db.changelog.gradle.plugin.task.ChangelogHistoryTask;
import com.github.toolarium.db.changelog.gradle.plugin.task.ChangelogRollbackTask;
import com.github.toolarium.db.changelog.gradle.plugin.task.ChangelogStatusTask;
import com.github.toolarium.db.changelog.gradle.plugin.task.ChangelogSyncTask;
import com.github.toolarium.db.changelog.gradle.plugin.task.ChangelogTagTask;
import com.github.toolarium.db.changelog.gradle.plugin.task.ChangelogUpdateTask;
import com.github.toolarium.db.changelog.gradle.plugin.task.ChangelogValidateTask;
import org.gradle.api.Plugin;
import org.gradle.api.Project;


/**
 * Gradle plugin for toolarium-db-changelog. Registers the {@code dbChangelog} extension
 * and all changelog tasks.
 *
 * @author patrick
 */
public class ToolariumDbChangelogPlugin implements Plugin<Project> {

    /** The task group name for all changelog tasks. */
    public static final String TASK_GROUP = "db-changelog";


    /**
     * Applies this plugin to the given project. Creates the {@code dbChangelog} extension
     * and registers all changelog tasks.
     *
     * @param project the target project
     *
     * @see org.gradle.api.Plugin#apply(java.lang.Object)
     */
    @Override
    public void apply(Project project) {
        DbChangelogExtension extension = project.getExtensions().create("dbChangelog", DbChangelogExtension.class, project.getObjects());

        registerTask(project, "changelogUpdate", ChangelogUpdateTask.class, "Applies all pending changesets to the database.", extension);
        registerTask(project, "changelogRollback", ChangelogRollbackTask.class, "Rolls back changesets by count, tag, or date.", extension);
        registerTask(project, "changelogStatus", ChangelogStatusTask.class, "Displays the status of pending and applied changesets.", extension);
        registerTask(project, "changelogValidate", ChangelogValidateTask.class, "Validates the changelog for errors and checksum mismatches.", extension);
        registerTask(project, "changelogGenerateSql", ChangelogGenerateSqlTask.class, "Generates the SQL that would be executed by an update (dry-run).", extension);
        registerTask(project, "changelogSync", ChangelogSyncTask.class, "Marks all pending changesets as executed without running them.", extension);
        registerTask(project, "changelogTag", ChangelogTagTask.class, "Tags the current database state for future rollback reference.", extension);
        registerTask(project, "changelogHistory", ChangelogHistoryTask.class, "Displays the history of applied changesets.", extension);
        registerTask(project, "changelogClearChecksums", ChangelogClearChecksumsTask.class, "Clears all stored checksums, forcing recalculation on the next run.", extension);
        registerTask(project, "changelogGenerateRollbackSql", ChangelogGenerateRollbackSqlTask.class, "Generates rollback SQL for the last N changesets without executing.", extension);
        registerTask(project, "changelogDropAll", ChangelogDropAllTask.class, "Drops all database objects managed by the changelog.", extension);
    }


    /**
     * Registers a changelog task with the given name, type, description, and extension.
     *
     * @param project the Gradle project
     * @param name the task name
     * @param type the task class
     * @param description the task description
     * @param extension the changelog extension
     * @param <T> the task type
     */
    private <T extends AbstractDbChangelogTask> void registerTask(Project project, String name, Class<T> type, String description, DbChangelogExtension extension) {
        project.getTasks().register(name, type, task -> {
            task.setGroup(TASK_GROUP);
            task.setDescription(description);
            task.getUrl().set(extension.getUrl());
            task.getUsername().set(extension.getUsername());
            task.getPassword().set(extension.getPassword());
            task.getDriver().set(extension.getDriver());
            task.getChangelog().set(extension.getChangelog());
            task.getContexts().set(extension.getContexts());
            task.getLabels().set(extension.getLabels());
            task.getChangelogTableName().set(extension.getChangelogTableName());
            task.getDefaultSchemaName().set(extension.getDefaultSchemaName());
            task.getLeaderElection().set(extension.getLeaderElection());
        });
    }
}

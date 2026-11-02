/*
 * ChangelogGenerateSqlTask.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.db.changelog.gradle.plugin.task;

//import com.github.toolarium.db.changelog.IChangelogRunner;
import com.github.toolarium.db.changelog.gradle.plugin.AbstractDbChangelogTask;
//import java.util.List;
import org.gradle.api.GradleException;
import org.gradle.api.tasks.TaskAction;


/**
 * Gradle task that generates the SQL that would be executed by an update (dry-run).
 *
 * @author patrick
 */
public class ChangelogGenerateSqlTask extends AbstractDbChangelogTask {

    /**
     * Executes the SQL generation.
     *
     * @throws GradleException if the SQL generation fails
     */
    @TaskAction
    public void executeGenerateSql() {
        getLogger().lifecycle("Generating SQL for pending changesets...");

        // TODO: uncomment when generateSql() is available in IChangelogRunner
        //try {
        //    IChangelogRunner runner = createRunner();
        //    List<String> sqlStatements = runner.generateSql();
        //
        //    if (sqlStatements.isEmpty()) {
        //        getLogger().lifecycle("No pending changesets. No SQL to generate.");
        //    } else {
        //        getLogger().lifecycle("Generated " + sqlStatements.size() + " SQL statement(s):");
        //        for (String sql : sqlStatements) {
        //            getLogger().lifecycle(sql);
        //        }
        //    }
        //} catch (Exception ex) {
        //    throw new GradleException("SQL generation failed: " + ex.getMessage(), ex);
        //}
        throw new GradleException("The generateSql operation is not yet available in IChangelogRunner.");
    }
}

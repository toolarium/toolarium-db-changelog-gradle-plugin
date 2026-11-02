/*
 * ChangelogTagTask.java
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
 * Gradle task that tags the current database state for future rollback reference.
 *
 * @author patrick
 */
public class ChangelogTagTask extends AbstractDbChangelogTask {
    private final Property<String> tagName;


    /**
     * Constructor for ChangelogTagTask.
     */
    public ChangelogTagTask() {
        tagName = getProject().getObjects().property(String.class);

        if (getProject().hasProperty("tagName")) {
            tagName.set(getProject().property("tagName").toString());
        }
    }


    /**
     * Gets the tag name to apply.
     *
     * @return the tagName property
     */
    @Input
    @Optional
    public Property<String> getTagName() {
        return tagName;
    }


    /**
     * Executes the tag operation.
     *
     * @throws GradleException if the tag operation fails or the tag name is missing
     */
    @TaskAction
    public void executeTag() {
        if (!tagName.isPresent()) {
            throw new GradleException("Tag name is required. Use -PtagName=TAG.");
        }

        String tag = tagName.get();
        getLogger().lifecycle("Tagging current database state as '" + tag + "'...");

        try {
            IChangelogRunner runner = createRunner();
            runner.tag(tag);
            getLogger().lifecycle("Database tagged as '" + tag + "' successfully.");
        } catch (GradleException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new GradleException("Tagging failed: " + ex.getMessage(), ex);
        }
    }
}

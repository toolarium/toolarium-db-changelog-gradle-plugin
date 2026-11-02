/*
 * DbChangelogExtension.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.db.changelog.gradle.plugin;

import org.gradle.api.model.ObjectFactory;
import org.gradle.api.provider.ListProperty;
import org.gradle.api.provider.Property;


/**
 * Extension block for configuring the toolarium-db-changelog plugin.
 *
 * <p>Usage in {@code build.gradle}:
 * <pre>
 * dbChangelog {
 *     url = 'jdbc:h2:mem:test'
 *     username = 'sa'
 *     password = ''
 *     changelog = 'src/main/resources/db/changelog/changelog-master.db.yaml'
 * }
 * </pre>
 *
 * @author patrick
 */
public class DbChangelogExtension {
    private final Property<String> url;
    private final Property<String> username;
    private final Property<String> password;
    private final Property<String> driver;
    private final Property<String> changelog;
    private final ListProperty<String> contexts;
    private final ListProperty<String> labels;
    private final Property<String> changelogTableName;
    private final Property<String> defaultSchemaName;
    private final Property<Boolean> leaderElection;
    private final Property<Integer> rollbackCount;
    private final Property<String> rollbackTag;
    private final Property<String> rollbackDate;


    /**
     * Constructor for DbChangelogExtension.
     *
     * @param objects the Gradle object factory
     */
    @javax.inject.Inject
    public DbChangelogExtension(ObjectFactory objects) {
        url = objects.property(String.class);
        username = objects.property(String.class);
        password = objects.property(String.class);
        driver = objects.property(String.class);
        changelog = objects.property(String.class)
                .convention("src/main/resources/db/changelog/changelog-master.db.yaml");
        contexts = objects.listProperty(String.class).empty();
        labels = objects.listProperty(String.class).empty();
        changelogTableName = objects.property(String.class).convention("DB_CHANGELOG");
        defaultSchemaName = objects.property(String.class);
        leaderElection = objects.property(Boolean.class).convention(false);
        rollbackCount = objects.property(Integer.class);
        rollbackTag = objects.property(String.class);
        rollbackDate = objects.property(String.class);
    }


    /**
     * Gets the JDBC URL.
     *
     * @return the url property
     */
    public Property<String> getUrl() {
        return url;
    }


    /**
     * Gets the database username.
     *
     * @return the username property
     */
    public Property<String> getUsername() {
        return username;
    }


    /**
     * Gets the database password.
     *
     * @return the password property
     */
    public Property<String> getPassword() {
        return password;
    }


    /**
     * Gets the JDBC driver class name.
     *
     * @return the driver property
     */
    public Property<String> getDriver() {
        return driver;
    }


    /**
     * Gets the changelog file path.
     *
     * @return the changelog property
     */
    public Property<String> getChangelog() {
        return changelog;
    }


    /**
     * Gets the active contexts for filtering changesets.
     *
     * @return the contexts list property
     */
    public ListProperty<String> getContexts() {
        return contexts;
    }


    /**
     * Gets the active labels for filtering changesets.
     *
     * @return the labels list property
     */
    public ListProperty<String> getLabels() {
        return labels;
    }


    /**
     * Gets the changelog tracking table name.
     *
     * @return the changelogTableName property
     */
    public Property<String> getChangelogTableName() {
        return changelogTableName;
    }


    /**
     * Gets the default schema name.
     *
     * @return the defaultSchemaName property
     */
    public Property<String> getDefaultSchemaName() {
        return defaultSchemaName;
    }


    /**
     * Gets whether leader election is enabled for distributed locking.
     *
     * @return the leaderElection property
     */
    public Property<Boolean> getLeaderElection() {
        return leaderElection;
    }


    /**
     * Gets the number of changesets to roll back.
     *
     * @return the rollbackCount property
     */
    public Property<Integer> getRollbackCount() {
        return rollbackCount;
    }


    /**
     * Gets the tag to roll back to.
     *
     * @return the rollbackTag property
     */
    public Property<String> getRollbackTag() {
        return rollbackTag;
    }


    /**
     * Gets the date to roll back to (ISO-8601 format).
     *
     * @return the rollbackDate property
     */
    public Property<String> getRollbackDate() {
        return rollbackDate;
    }
}

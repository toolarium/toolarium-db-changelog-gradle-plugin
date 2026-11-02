/*
 * AbstractDbChangelogTask.java
 *
 * Copyright by toolarium, all rights reserved.
 */
package com.github.toolarium.db.changelog.gradle.plugin;

import com.github.toolarium.db.changelog.ChangelogRunnerBuilder;
import com.github.toolarium.db.changelog.IChangelogRunner;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Logger;
import javax.sql.DataSource;
import org.gradle.api.DefaultTask;
import org.gradle.api.GradleException;
import org.gradle.api.provider.ListProperty;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.Internal;
import org.gradle.api.tasks.Optional;


/**
 * Abstract base class for all changelog tasks. Provides common properties
 * and the changelog runner creation logic.
 *
 * @author patrick
 */
public abstract class AbstractDbChangelogTask extends DefaultTask {

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


    /**
     * Constructor for AbstractDbChangelogTask.
     */
    protected AbstractDbChangelogTask() {
        url = getProject().getObjects().property(String.class);
        username = getProject().getObjects().property(String.class);
        password = getProject().getObjects().property(String.class);
        driver = getProject().getObjects().property(String.class);
        changelog = getProject().getObjects().property(String.class);
        contexts = getProject().getObjects().listProperty(String.class).empty();
        labels = getProject().getObjects().listProperty(String.class).empty();
        changelogTableName = getProject().getObjects().property(String.class);
        defaultSchemaName = getProject().getObjects().property(String.class);
        leaderElection = getProject().getObjects().property(Boolean.class).convention(false);
    }


    /**
     * Gets the JDBC URL.
     *
     * @return the url property
     */
    @Input
    public Property<String> getUrl() {
        return url;
    }


    /**
     * Gets the database username.
     *
     * @return the username property
     */
    @Input
    @Optional
    public Property<String> getUsername() {
        return username;
    }


    /**
     * Gets the database password. Marked as {@code @Internal} to prevent the password
     * from appearing in build cache keys, build scans, or CI log output.
     *
     * @return the password property
     */
    @Internal
    public Property<String> getPassword() {
        return password;
    }


    /**
     * Gets the JDBC driver class name.
     *
     * @return the driver property
     */
    @Input
    @Optional
    public Property<String> getDriver() {
        return driver;
    }


    /**
     * Gets the changelog file path.
     *
     * @return the changelog property
     */
    @Input
    public Property<String> getChangelog() {
        return changelog;
    }


    /**
     * Gets the active contexts for filtering changesets.
     *
     * @return the contexts list property
     */
    @Input
    @Optional
    public ListProperty<String> getContexts() {
        return contexts;
    }


    /**
     * Gets the active labels for filtering changesets.
     *
     * @return the labels list property
     */
    @Input
    @Optional
    public ListProperty<String> getLabels() {
        return labels;
    }


    /**
     * Gets the changelog tracking table name.
     *
     * @return the changelogTableName property
     */
    @Input
    @Optional
    public Property<String> getChangelogTableName() {
        return changelogTableName;
    }


    /**
     * Gets the default schema name.
     *
     * @return the defaultSchemaName property
     */
    @Input
    @Optional
    public Property<String> getDefaultSchemaName() {
        return defaultSchemaName;
    }


    /**
     * Gets whether leader election is enabled for distributed locking.
     *
     * @return the leaderElection property
     */
    @Input
    @Optional
    public Property<Boolean> getLeaderElection() {
        return leaderElection;
    }


    /**
     * Creates a configured {@link IChangelogRunner} instance from the task properties.
     *
     * @return the configured changelog runner
     * @throws GradleException if required properties are missing or the driver cannot be loaded
     */
    protected IChangelogRunner createRunner() {
        if (!url.isPresent()) {
            throw new GradleException("Database URL is required. Configure it in the dbChangelog extension block.");
        }

        if (!changelog.isPresent()) {
            throw new GradleException("Changelog path is required. Configure it in the dbChangelog extension block.");
        }

        DataSource dataSource = createDataSource();

        ChangelogRunnerBuilder builder = ChangelogRunnerBuilder.builder()
                .dataSource(dataSource)
                .changelog(changelog.get());

        if (contexts.isPresent() && !contexts.get().isEmpty()) {
            builder.contexts(contexts.get());
        }

        if (labels.isPresent() && !labels.get().isEmpty()) {
            builder.labels(labels.get());
        }

        if (changelogTableName.isPresent()) {
            builder.trackingTable(changelogTableName.get());
        }

        // TODO: uncomment when defaultSchemaName is available in ChangelogRunnerBuilder
        //if (defaultSchemaName.isPresent()) {
        //    builder.defaultSchemaName(defaultSchemaName.get());
        //}

        if (leaderElection.isPresent() && leaderElection.get()) {
            builder.leaderElection(true);
        }

        return builder.build();
    }


    /**
     * Creates a {@link DataSource} from the configured JDBC properties.
     *
     * @return the data source
     * @throws GradleException if the JDBC driver class cannot be found
     */
    private DataSource createDataSource() {
        if (driver.isPresent()) {
            try {
                Class.forName(driver.get());
            } catch (ClassNotFoundException e) {
                throw new GradleException("JDBC driver class not found: " + driver.get(), e);
            }
        }

        return new SimpleDataSource(url.get(), username.getOrNull(), password.getOrNull());
    }


    /**
     * A minimal {@link DataSource} implementation that creates connections via {@link DriverManager}.
     *
     * @author patrick
     */
    private static final class SimpleDataSource implements DataSource {
        private final String jdbcUrl;
        private final String dbUsername;
        private final String dbPassword;


        /**
         * Constructor for SimpleDataSource.
         *
         * @param jdbcUrl the JDBC URL
         * @param dbUsername the database username (may be null)
         * @param dbPassword the database password (may be null)
         */
        SimpleDataSource(String jdbcUrl, String dbUsername, String dbPassword) {
            this.jdbcUrl = jdbcUrl;
            this.dbUsername = dbUsername;
            this.dbPassword = dbPassword;
        }


        /**
         * Creates a new database connection using the configured JDBC URL and credentials.
         *
         * @return a new connection
         * @throws SQLException if a database access error occurs
         *
         * @see javax.sql.DataSource#getConnection()
         */
        @Override
        public Connection getConnection() throws SQLException {
            if (dbUsername != null) {
                return DriverManager.getConnection(jdbcUrl, dbUsername, dbPassword);
            }
            return DriverManager.getConnection(jdbcUrl);
        }


        /**
         * Creates a new database connection using the given credentials instead of the
         * configured ones.
         *
         * @param user the database username
         * @param pass the database password
         * @return a new connection
         * @throws SQLException if a database access error occurs
         *
         * @see javax.sql.DataSource#getConnection(java.lang.String, java.lang.String)
         */
        @Override
        public Connection getConnection(String user, String pass) throws SQLException {
            return DriverManager.getConnection(jdbcUrl, user, pass);
        }


        /**
         * Returns the log writer. This implementation always returns null.
         *
         * @return null (not supported)
         *
         * @see javax.sql.CommonDataSource#getLogWriter()
         */
        @Override
        public PrintWriter getLogWriter() {
            return null;
        }


        /**
         * Sets the log writer. This operation is not supported by this implementation.
         *
         * @param out the log writer (ignored)
         *
         * @see javax.sql.CommonDataSource#setLogWriter(java.io.PrintWriter)
         */
        @Override
        public void setLogWriter(PrintWriter out) {
            // not supported
        }


        /**
         * Sets the login timeout. This operation is not supported by this implementation.
         *
         * @param seconds the login timeout in seconds (ignored)
         *
         * @see javax.sql.CommonDataSource#setLoginTimeout(int)
         */
        @Override
        public void setLoginTimeout(int seconds) {
            // not supported
        }


        /**
         * Returns the login timeout. This implementation always returns zero.
         *
         * @return 0 (not supported)
         *
         * @see javax.sql.CommonDataSource#getLoginTimeout()
         */
        @Override
        public int getLoginTimeout() {
            return 0;
        }


        /**
         * Returns the parent logger for this data source.
         *
         * @return the global logger
         *
         * @see javax.sql.CommonDataSource#getParentLogger()
         */
        @Override
        public Logger getParentLogger() {
            return Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
        }


        /**
         * Returns an object that implements the given interface. This implementation
         * does not support unwrapping.
         *
         * @param <T> the type of the class modeled by the iface parameter
         * @param iface the interface class to unwrap to
         * @return never returns normally
         * @throws SQLException always, as this implementation does not support unwrapping
         *
         * @see java.sql.Wrapper#unwrap(java.lang.Class)
         */
        @Override
        public <T> T unwrap(Class<T> iface) throws SQLException {
            throw new SQLException("Not a wrapper for " + iface.getName());
        }


        /**
         * Returns false, as this implementation does not wrap any other object.
         *
         * @param iface the interface class to check
         * @return false always
         *
         * @see java.sql.Wrapper#isWrapperFor(java.lang.Class)
         */
        @Override
        public boolean isWrapperFor(Class<?> iface) {
            return false;
        }
    }
}

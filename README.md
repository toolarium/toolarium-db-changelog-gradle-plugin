[![License](https://img.shields.io/github/license/toolarium/toolarium-db-changelog-gradle-plugin)](https://github.com/toolarium/toolarium-db-changelog-gradle-plugin/blob/master/LICENSE)
[![Maven Central](https://img.shields.io/maven-central/v/com.github.toolarium/toolarium-db-changelog-gradle-plugin/0.8.0)](https://search.maven.org/artifact/com.github.toolarium/toolarium-db-changelog-gradle-plugin/0.8.0/jar)
[![javadoc](https://javadoc.io/badge2/com.github.toolarium/toolarium-db-changelog-gradle-plugin/javadoc.svg)](https://javadoc.io/doc/com.github.toolarium/toolarium-db-changelog-gradle-plugin)

# toolarium-db-changelog-gradle-plugin

A Gradle plugin that provides build tasks for the [toolarium-db-changelog](https://github.com/toolarium/toolarium-db-changelog) library, supporting update, rollback, status, validate, tag, history, sync, clear checksums, and dry-run SQL generation.


## Applying the plugin

**Groovy DSL** (`build.gradle`):
```groovy
plugins {
    id 'com.github.toolarium.db-changelog' version '0.8.0'
}
```

**Kotlin DSL** (`build.gradle.kts`):
```kotlin
plugins {
    id("com.github.toolarium.db-changelog") version "0.8.0"
}
```


## Configuration

Configure the plugin via the `dbChangelog` extension block:

```groovy
dbChangelog {
    url = 'jdbc:postgresql://localhost:5432/mydb'
    username = 'dbuser'
    password = 'secret'
    driver = 'org.postgresql.Driver'
    changelog = 'src/main/resources/db/changelog/changelog-master.db.yaml'
    contexts = ['production']
    labels = ['v1.0']
    changelogTableName = 'DB_CHANGELOG'
    defaultSchemaName = 'public'
    leaderElection = false
}
```

| Property | Default | Description |
|---|---|---|
| `url` | (required) | JDBC connection URL |
| `username` | (none) | Database username |
| `password` | (none) | Database password |
| `driver` | (auto-detect) | JDBC driver class name |
| `changelog` | `src/main/resources/db/changelog/changelog-master.db.yaml` | Path to the changelog file |
| `contexts` | (empty) | Active contexts for filtering changesets |
| `labels` | (empty) | Active labels for filtering changesets |
| `changelogTableName` | `DB_CHANGELOG` | Name of the tracking table |
| `defaultSchemaName` | (none) | Default database schema |
| `leaderElection` | `false` | Enable distributed locking via leader election |
| `rollbackCount` | (none) | Number of changesets to roll back |
| `rollbackTag` | (none) | Tag to roll back to |
| `rollbackDate` | (none) | ISO-8601 date to roll back to |


## Tasks

All tasks are registered under the `db-changelog` group.

| Task | Description |
|---|---|
| `changelogUpdate` | Applies all pending changesets (or up to N with `-PupdateCount=N`) |
| `changelogRollback` | Rolls back changesets by count, tag, or date |
| `changelogStatus` | Displays the status of pending changesets |
| `changelogValidate` | Validates the changelog for errors and checksum mismatches |
| `changelogTag` | Tags the current database state (requires `-PtagName=TAG`) |
| `changelogHistory` | Displays the history of applied changesets |
| `changelogSync` | Marks all pending changesets as executed without running them |
| `changelogClearChecksums` | Clears all stored checksums, forcing recalculation on the next run |
| `changelogGenerateRollbackSql` | Generates rollback SQL for the last N changesets (requires `-ProllbackCount=N`) |
| `changelogGenerateSql` | Generates the SQL that would be executed by an update (dry-run, not yet available) |
| `changelogDropAll` | Drops all database objects managed by the changelog (not yet available) |


## Usage examples

```bash
# Apply all pending migrations
gradle changelogUpdate

# Apply up to 5 pending migrations
gradle changelogUpdate -PupdateCount=5

# Check pending changesets
gradle changelogStatus

# Show applied changeset history
gradle changelogHistory

# Validate changelog
gradle changelogValidate

# Tag the current state
gradle changelogTag -PtagName=v1.0

# Roll back the last 3 changesets
gradle changelogRollback -ProllbackCount=3

# Roll back to a tag
gradle changelogRollback -ProllbackTag=v1.0

# Roll back to a specific date
gradle changelogRollback -ProllbackDate=2026-01-15T10:00:00Z

# Generate rollback SQL without executing
gradle changelogGenerateRollbackSql -ProllbackCount=3

# Mark all pending changesets as applied (baselining)
gradle changelogSync

# Clear all checksums (forces recalculation)
gradle changelogClearChecksums
```


## Built With

* [cb](https://github.com/toolarium/common-build) - The toolarium common build


## Versioning

We use [SemVer](http://semver.org/) for versioning. For the versions available, see the [tags on this repository](https://github.com/toolarium/toolarium-db-changelog-gradle-plugin/tags).


## License

This project is licensed under the MPL-2.0 License, see the [LICENSE](LICENSE) file for details.

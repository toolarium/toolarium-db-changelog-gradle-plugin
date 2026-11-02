# toolarium-db-changelog-gradle-plugin

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [ 0.8.1 ] - 2026-11-02

## [ 0.8.0 ] - 2026-11-02
### Added
- Gradle plugin with `dbChangelog` extension block for configuring database connection and changelog settings.
- `changelogUpdate` task: applies all pending changesets to the database (supports `-PupdateCount=N` for partial updates).
- `changelogRollback` task: rolls back changesets by count, tag, or date.
- `changelogStatus` task: displays the status of pending changesets.
- `changelogValidate` task: validates the changelog for errors and checksum mismatches.
- `changelogTag` task: tags the current database state for future rollback reference (requires `-PtagName=TAG`).
- `changelogHistory` task: displays the history of applied changesets.
- `changelogSync` task: marks all pending changesets as executed without running them (baselining).
- `changelogClearChecksums` task: clears all stored checksums, forcing recalculation on the next run.
- `changelogGenerateRollbackSql` task: generates rollback SQL for the last N changesets without executing (requires `-ProllbackCount=N`).
- `changelogGenerateSql` task: placeholder for future dry-run SQL generation (not yet available in the library).
- `changelogDropAll` task: placeholder for dropping all database objects (not yet available in the library).
- Support for context and label filtering.
- Configurable tracking table name, default schema, and leader election.
- Rollback via project properties: `-ProllbackCount`, `-ProllbackTag`, `-ProllbackDate`.
- Input validation with clear error messages for invalid property values.
- Password excluded from Gradle build cache and log output.

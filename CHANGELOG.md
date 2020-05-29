# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres poorly to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

## [0.7.1] - 2020-05-29

Redeploy with correct artifacts.

## [0.7.0] - 2020-05-25
### Added
- Annotation-based `ConfigExtension` concept.
- `LiquibaseExtension` for running migrations after configuration.
- `DbUnitExtension` for loading dev datasets.
- `nullable` property for marking config properties as nullable. Can be used only with object representations of primitive values (`Boolean`, `Integer`, `Long`, `Float`, `Double`) and `String`.

### Changed
- Improved error reporting. The reporting is now clearly split into distinct initializer parsing and initialization phases to allow more fine grained-error reporting.
- Throw `IllegalArgumentException`s instead of dedicated `MappingException`s.
- Split the core JDBC configuration into a separate module.
- Renamed `LogbackConfig` to `LogbackConfiguration` to keep the naming scheme consistent.

### Deprecated
- `MappingException` is not thrown by any method anymore.
- `SqlDatabaseConfig` should not be used anymore. `JdbcConfiguration` extends the deprecated implementation for now.

## [0.6.0] - 2020-05-18
### Added
- `ConfigProperty` annotation to replace `EasyConfigProperty`.
- `EnvProviderProperty` annotation for injecting current `EnvProvider` to configuration.
- `EnvProvider::getVariables` for reading the currently declared environment variables.
- `LogbackConfig` for extending the default logback configuration.

### Deprecated
- Deprecated `EasyConfigProperty` annotation.

## [0.5.0] - 2020-03-06
### Added
- Add `JOOQ_DRIVER_CLASS_NAME` parameter. Running multiple jdbc drivers can cause issues
  if an explicit driver class is not defied.

## [0.4.3] - 2019-12-27
### Changed
- Use `com.google.code.findbugs:annotations` instead of `com.google.code.findbugs:jsr305`.

## [0.4.2] - 2019-11-15
### Added
- Support empty primitive lists when an empty string as default value.

## [0.4.1] - 2019-11-07
### Added
- Support defining maximum connection pool size for JOOQ.

## [0.4.0] - 2019-11-01
### Added
- Add module for `fi.jubic:snoozy` `ServerConfiguration`.

## [0.3.3] - 2019-10-31
### Fixed
- Fix multi-level nesting of config objects.

## [0.3.2] - 2019-10-18
### Security
- Update dependencies.

## [0.3.1] - 2019-10-18
### Added
- Support lists of objects using a prefix with list index placeholder.

### Changed
- Allow providing an empty default value indicating an optional value with
  an empty default.

## [0.3.0] - 2019-10-15
### Changed
- Fix typo in annotations package name. This change breaks all usages.

## [0.2.0] - 2019-10-15
### Added
- Add dotenv support by default.

### Changed
- Improved exception messages on mapping/parsing failures.

## [0.1.10] - 2019-06-03
### Security
- Update dependencies.

## [0.1.9] - 2019-05-24
### Added
- Add warning message if multiple connection pools are initialized with the same
  parameters.

## [0.1.8] - 2019-04-25
### Added
- Java 11 support.

## [0.1.7] - 2019-03-17
### Changed
- Use HikariCP in JOOQ module.

## [0.1.6] - 2019-03-07
### Changed
- Initialize JOOQ `Configuration` already during mapping. Possible exception are
  caught, wrapped into `MappingException` and thrown again.

## [0.1.5] - 2019-02-20

## [0.1.4] - 2019-02-18
### Added
- Add initial `SqlDatabaseConfig` interface.
- Add JOOQ module and `JooqConfiguration`.

## [0.1.3] - 2019-02-11

Initial release.

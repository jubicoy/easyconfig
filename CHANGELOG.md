# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres poorly to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

## [0.10.6] - 2024-12-16
### Changed
- Bump logback.version from 1.5.9 to 1.5.12.
- Bump org.liquibase:liquibase-core from 4.29.2 to 4.30.0.
- Bump com.zaxxer:HikariCP from 6.0.0 to 6.2.1.
- Bump io.github.cdimascio:dotenv-java from 3.0.2 to 3.1.0.
- Bump fi.jubic:easyparent from 0.1.15 to 0.1.16.
- Bump fi.jubic:snoozy-core from 0.10.4 to 0.10.5.

## [0.10.5] - 2024-10-09
### Changed
- Bump logback.version from 1.5.7 to 1.5.8.
- Bump fi.jubic:snoozy-core from 0.10.2 to 0.10.4.
- Bump com.zaxxer:HikariCP from 5.1.0 to 6.0.0.
- Bump logback.version from 1.5.8 to 1.5.9.
- Bump fi.jubic:easyparent from 0.1.14 to 0.1.15.

## [0.10.4] - 2024-09-05
### Changed
- Bump fi.jubic:easyparent from 0.1.13 to 0.1.14.
- Bump org.liquibase:liquibase-core from 4.29.1 to 4.29.2.
- Bump io.github.cdimascio:dotenv-java from 3.0.1 to 3.0.2.

## [0.10.3] - 2024-08-31
### Changed
- Bump fi.jubic:easyparent from 0.1.12 to 0.1.13.
- Bump fi.jubic:snoozy-core from 0.10.1 to 0.10.2.
- Bump org.slf4j:slf4j-api from 2.0.13 to 2.0.16.
- Bump logback.version from 1.5.6 to 1.5.7.

## [0.10.2] - 2024-08-31
### Changed
- Bump org.dbunit:dbunit from 2.7.3 to 2.8.0.
- Bump org.freemarker:freemarker from 2.3.32 to 2.3.33.
- Bump org.liquibase:liquibase-core from 4.27.0 to 4.29.1.
- Bump io.github.cdimascio:dotenv-java from 3.0.0 to 3.0.1.

## [0.10.1] - 2024-05-07
### Changed
- Bump fi.jubic:easyparent from 0.1.11 to 0.1.12.
- Bump fi.jubic:snoozy-core from 0.10.0 to 0.10.1.
- Bump logback.version from 1.4.14 to 1.5.6.
- Bump org.slf4j:slf4j-api from 2.0.9 to 2.0.13.
- Bump org.liquibase:liquibase-core from 4.25.0 to 4.27.0.
- Bump org.codehaus.janino:janino from 3.1.11 to 3.1.12.

## [0.10.0] - 2023-12-13
### Added
- Java 21 tests.

## Changed
- Bump fi.jubic:snoozy-core from 0.9.0 to 0.10.0.
- Bump fi.jubic:easyparent to 0.1.11.
- Bump org.slf4j:slf4j-api tp 2.0.9.
- Bump org.codehaus.janino:janino from 3.1.6 to 3.1.11.
- Bump ch.qos.logback:logback-core from 1.2.10 to 1.4.14.
- Bump liquibase-slf4j 5.0.0 and liquibase-core to 4.25.0.
- Bump io.github.cdimascio:dotenv-java from 2.2.3 to 3.0.0.
- Bump com.zaxxer:HikariCP from 3.4.5 to 5.1.0.
- Bump freemarker from 2.3.31 to 2.3.32.


## [0.9.2] - 2022-09-02
### Added
- Configuration parameters to control connection allocation in PooledJdbcConfiguration.

### Fixed
- PooledJdbcConfiguration connection leak if an exception is thrown when applying connectionFunction.

## [0.9.1] - 2022-06-20
### Security
- Update parent to 0.1.5.
- Update liquibase-core to 4.8.0.
- Update snoozy to 0.9.0.

## [0.9.0] - 2022-01-05
### Removed
- Dropped Java 8 support.

### Security
- Update freemarker to 2.3.31.
- Update snoozy to 0.8.1.
- Update logback to 1.2.10.
- Update easyparent to 0.1.3.

## [0.8.4] - 2021-12-13
### Fixed
- Downgrade HikariCP to maintain slf4j 1.7 compatibility.

## [0.8.3] - 2021-12-10
### Added
- liquibase-slf4j for slf4j based logging from liquibase.

## [0.8.2] - 2021-12-10
### Added
- Full Java 17 support.

## [0.8.1] - 2021-08-09
### Changed
- Update DbUnit to 2.7.2

## [0.8.0] - 2021-04-13
### Added
- `noPrefix` parameter to `ConfigProperty` for reading properties from root namespace.
- Snoozy Swagger configuration support.

### Changed
- Update JOOQ to 3.14.4.

### Security
- Update dependencies.
- Update snoozy to 0.7.1.
- Use DBUnit `2.7.1-SNAPSHOT` to avoid pre-4.1.0 Apache POI.

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

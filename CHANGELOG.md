# Changelog - see https://keepachangelog.com for conventions

## [Unreleased]

### Added

### Changed

### Deprecated

### Removed

### Fixed

## [2.7.2] - 2021-02-03

### Changed

- Upgrading to arch-unit-build-plugin-core 2.7.2

## [2.7.1] - 2021-01-27

### Changed

- Upgrading to arch-unit-build-plugin-core 2.7.1

## [2.7.0] - 2021-01-22

### Changed

- PR #42 - Upgrading to arch-unit-build-plugin-core 2.7.0 for Java 15 compatibility

## [2.6.2] - 2020-09-21

### Changed
- PR #39 - Now getting the main and test values in MavenScopePathProvider from Maven project variable, instead of hardcoding it


## [2.6.1] - 2020-08-20

### Changed
- PR #38 - Breaking change : "projectPath" is not a valid property anymore. since we are in Maven context, we're using MavenScopePathProvider which provides the required values
- upgrading to arch-unit-core 2.6.1


## [2.4.0] - 2020-03-23

### Changed
- using latest Mockito so that it runs fine with JDK 11
- upgrading Archunit plugin core to 2.5.1 : now can configure excludedPaths

## [2.3.0] - 2019-10-06

### Changed
- PR #30 - Now using https://github.com/societe-generale/arch-unit-build-plugin-core as the foundation for the maven plugin 
- PR #27 - setting Maven plugins versions to avoid warning at build time - thanks [@khmarbaise](https://github.com/khmarbaise) for the contribution !
- PR #25 - not scanning "pom" projects - thanks [@khmarbaise](https://github.com/khmarbaise) for the contribution !
- PR #20 - using Maven recommended way for logging - thanks [@croesh](https://github.com/croesh) for the contribution !
- upgrading Lombok and Mockito to latest versions to be compatible with latest JDK when building

## [2.2.0] - 2019-07-31

### Added
- PR #14 - new rule available out of the box : no public fields - thanks [@FanJups](https://github.com/FanJups) for the contribution !
- PR #17 - skip parameter to skip configurable rules or whole plugin execution - thanks [@croesh](https://github.com/croesch) for the contribution !

### Changed
- upgraded to ArchUnit 0.11.0

### Fixed
- PR #16 - will now allow to use scope with empty package + test structure refactoring - thanks [@croesh](https://github.com/croesch) for the contribution !

## [2.1.0] - 2019-07-12

### Changed
- PR #15 - will now fail if test to execute is not found
- PR #15 - all checks of a configurableRule will execute by default
- PR #15 - pretty big refactoring, to improve overall design
--> Thanks a lot to [@codecholeric](https://github.com/codecholeric) for the contribution ! 

## [2.0.0] - 2019-05-19

### Changed
- PR #6 - BREAKING CHANGE : config has changed to enable the use of preconfigured rule (as before) and configurable rules 
- refactoring, cleaning up ArchUtils

### Added
- PR #5 - new preconfigured rule available : NoJavaUtilDateRuleTest - thanks [@FanJups](https://github.com/FanJups) for the contribution ! 


## [1.0.2] - 2019-05-06

### Added
- new rules : NoInjectedFieldTest and NoAutowiredFieldTest - thanks [@nils-christian](https://github.com/nils-christian) for the idea !

### Changed
- upgraded to ArchUnit 0.10.2
- NoJunitAssertRuleTest now also catches JUnit 5 asserts

### Fixed
- Coverall code coverage is now being reported

## [1.0.1] - 2019-02-28

### Changed
- upgraded to latest ArchUnit version - 0.9.3
- cleaned up pom.xml
- documentation

## [1.0.0] - 2019-02-27

first version !

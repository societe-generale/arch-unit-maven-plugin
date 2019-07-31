# Changelog - see https://keepachangelog.com for conventions

## [Unreleased]

### Added

### Changed

### Deprecated

### Removed

### Fixed

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

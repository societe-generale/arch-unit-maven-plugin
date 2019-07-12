# Changelog - see https://keepachangelog.com for conventions

## [Unreleased]

### Added

### Changed
- PR #15 - will now fail if test to execute is not found
- PR #15 - all checks of a configurableRule will execute by default
- PR #15 - pretty big refactoring, to improve overall design
--> Thanks a lot to [@codecholeric](https://github.com/codecholeric) for the contribution ! 

### Deprecated

### Removed

### Fixed

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

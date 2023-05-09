# Microsoft Intune App SDK for Android Sample Apps

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [2.3] - 2023-05-09

### Added

- Added integration with the Trusted Roots Certificates Management API.

### Changed

- Updated MAM SDK to version `9.5.0`.
- Updated MSAL to version `4.1.0`.
- Android:
  - Updated `compileSdkVersion` and `targetSdkVersion` to `33`.
  - Updated `minSdkVersion` to match the MAM SDK's `minSdkVersion` of `26`.
- React Native:
  - Updated React Native to version `0.70.6`.
  - Updated `compileSdkVersion` and `targetSdkVersion` to `33`.
  - Updated `minSdkVersion` to match the MAM SDK's `minSdkVersion` of `26`.

### Fixed

- Updated Javassist dependency to `3.27.0-GA`, which was required as of MAM SDK `7.0`.

## [2.2] - 2021-11-15

### Changed

- Updated MAM SDK to version `8.1.1`.
- Updated MSAL to version `2.0.8`.
- Android:
  - Updated `compileSdkVersion` and `targetSdkVersion` to `31`.
  - Updated `minSdkVersion` to match the MAM SDK's `minSdkVersion` of `23`.

## [2.1] - 2020-10-28

## Fixed

- Removed legacy ADAL authentication files.

## [2.0] - 2020-10-19

### Added

- Added a React Native MAM integrated app with specific developer guidance (see `ReadMe.md` in ReactMAM).

### Changed

- Updated MAM SDK to version `6.7.0`.
- Converted the application from ADAL to MSAL authentication.

## [1.4]  - 2020-01-31

### Added

- Added authentication value verification method.

### Changed

- Updated MAM SDK to version `6.3.0`.

## [1.3] - 2019-11-19

### Added

- Added Microsoft `SECURITY.md`.

### Changed

- Updated MAM SDK to version `6.0.0`.
- Converted legacy support libraries to the AndroidX support libraries.
- Forced `Taskr` to target the Company Portal for policy.

## [1.2] - 2019-06-04

### Changed

- Updated MAM SDK to version `5.7.1`, which is compatible with Instant Run.

## [1.1] - 2019-02-05

### Added

- Added ADAL logging for authentication troubleshooting.

### Changed

- Removed OneDrive functionality that was not fully implemented.
- Updated `ReadMe.md` and code comments for a better development experience.

### Fixed

- Fixed resource files to prevent crashing on API 23 and below.

## [1.0] - 2018-08-08

- Initial release of Taskr.

<!-- Links -->
[Unreleased]: https://github.com/msintuneappsdk/Taskr-Sample-Intune-Android-App/compare/v2.2...HEAD
[2.2]: https://github.com/msintuneappsdk/Taskr-Sample-Intune-Android-App/compare/v2.0...v2.2

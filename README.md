# RadSim Translation Model

[![Gradle Build](https://github.com/radsim/translation-model/actions/workflows/gradle_build.yml/badge.svg)](https://github.com/radsim/translation-model/actions/workflows/gradle_build.yml)
[![Gradle Publish](https://github.com/radsim/translation-model/actions/workflows/gradle_publish.yml/badge.svg)](https://github.com/radsim/translation-model/actions/workflows/gradle_publish.yml)

This project contains the Library for mapping OSM tags to RadSim tags with bidirectional logic.

If you require this software under a closed source license for you own
projects, please [contact us](https://www.cyface.de/#kontakt).

Changes between versions are found in the [Release
Section](https://github.com/radsim/translation-model/releases).

# Overview

- [Developer Guide](#developer-guide)
    - [Architecture](#architecture)
    - [Development Commands](#development-commands)
    - [GitHub Workflows](#github-workflows)
    - [Release a new Version](#release-a-new-version)
    - [Publishing Artifacts to GitHub Packages
      manually](#publishing-artifacts-to-github-packages-manually)
- [License](#license)

-----------------------------------------------------------------------------

## Developer Guide

This section is only relevant for developers of this library.

The library uses [Gradle](https://gradle.org/) as the build system

### Architecture

The library maps the tags in two directions. The RadSim backend uses it.

- **OSM tags**: the OpenStreetMap data, for example `highway=cycleway`, `bicycle=designated`.
- **RadSim tags**: the simplified tags of the simulation, for example `roadStyle=BicycleWay`, `surface=Comfort1Asphalt`.

**OSM to RadSim**: `ToRadSimMapper.map()` calls the `toRadSim()` functions of `SurfaceType`, `BikeInfrastructure`, `Speed` and `NumberOfLanes`.
`BikeInfrastructure.toRadSim()` checks the tags in this order: service and access, cycle highway, bicycle road, the infrastructure on the right side (bicycle way, lane, bus lane, mixed way, motor traffic), the infrastructure on the left side, and then `NO`.
The order is important: the infrastructure with the higher priority comes first.
Each `toRadSim()` function calls `TagFormatValidator.requireOsmFormat()` first. It throws an `IllegalArgumentException` if it gets RadSim tags (`roadStyle`, `roadStyleSimplified`) instead of OSM tags.

**RadSim to OSM**: `RadSimDeltaEngine.computeDelta()` returns the tags to add or to remove (a tag with an empty value means: remove the tag).
`BackMappingRules` contains the rules R1-R22 of the matrix of 2025-08-21 (BIK-1440) for the changes between the `SimplifiedBikeInfrastructure` categories. The rules look at the current `highway` tag.
The engine applies the rules step by step (for example `NO` to `BICYCLE_ROAD` to `BICYCLE_WAY`). It checks that each step changes the category and stops cycles.

There are two models of the bicycle infrastructure:

- `BikeInfrastructure`: the detailed values for the left and the right side, for example `BICYCLE_WAY_RIGHT_LANE_LEFT`. Each value has a `simplified` property.
- `SimplifiedBikeInfrastructure`: the 7 categories for the clients (RadSim tag `roadStyleSimplified`): `CYCLE_HIGHWAY`, `BICYCLE_ROAD`, `BICYCLE_WAY`, `BICYCLE_LANE`, `BUS_LANE`, `MIXED_WAY`, `NO`.

To add a back-mapping rule:

1. Add the rule to `BackMappingRules.rules` with a `RuleKey(from, to)`.
2. Return the delta as `Set<OsmTag>`.
3. Test that the delta, with `BikeInfrastructure.toRadSim()`, gives the target category (`BackMappingRulesTest` or `BackMappingMatrixTest`).

The category `NO` must never have `highway=cycleway` (BIK-1440).

### Development Commands

The build uses Kotlin 2.4.21 with the JVM toolchain 17. `OsmTag` comes from `de.cyface:model` (Cyface serialization).

```bash
./gradlew build                          # Build and test
./gradlew test --tests "BikeInfrastructureTest"  # Single test class
./gradlew detekt                         # Static analysis
```

The tests are parameterized JUnit 6 tests (`@ParameterizedTest` with `@MethodSource`) with Hamcrest matchers. Mark the test cases of a bug with the ticket ID, for example `[BIK-1478]`.

### GitHub Workflows

* `gradle_build.yml` builds and tests each push and pull request to `main` and `release-*`. Pull requests from forks are skipped.
* `gradle_publish.yml` runs on version tags: it publishes the artifacts to GitHub Packages and creates the GitHub release.

Rules of the workflows (hardening of CY-6748): do not use `${{ github.ref_name }}` or other event values directly in a `run:` script, pass them through `env:`; pass secrets through `env:` of the step that needs them; use `runs-on: ubuntu-26.04`, not `ubuntu-latest`.

The organization-wide cleanup in `radsim/production` (`.github/workflows/registry_cleanup.yml`) deletes old package versions.

### Release a new Version

See [Cyface Collector
Readme](https://github.com/cyface-de/data-collector#release-a-new-version)

- `version` in root `build.gradle.kts` is automatically set by the CI
- Just tag the release and push the tag to GitHub. Use a version without `v`, for example `2.6.9`, and an annotated tag: the body of the tag message becomes the release notes.
- A tag with a suffix (for example `2.6.9-beta1`) becomes a pre-release.
- The GitHub package is automatically published when a new version is
  tagged and pushed by our [GitHub
  Actions](https://github.com/radsim/translation-model/actions) to the [GitHub
  Registry](https://github.com/radsim/translation-model/packages)
- The tag is automatically marked as a *new Release* on
  [GitHub](https://github.com/radsim/translation-model/releases)


### Publishing artifacts to GitHub Packages manually

The artifacts produced by this project are distributed via
[GitHubPackages](https://github.com/features/packages). Before you can
publish artifacts you need to rename `gradle.properties.template` to
`gradle.properties` and enter your GitHub credentials. How to obtain
these credentials is described
[here](https://help.github.com/en/github/managing-packages-with-github-packages/about-github-packages#about-tokens).

To publish a new version of an artifact you need to:

1.  Increase the version number of the subproject within the
    `build.gradle.kts` file

2.  Call `./gradlew publish`

This will upload a new artifact to GitHub packages with the new version.
GitHub Packages will not accept to overwrite an existing version or to
upload a lower version. This project uses [semantic
versioning](https://semver.org/).


## License

Copyright 2019-2024 Cyface GmbH

This file is part of the RadSim Translation Model.

The RadSim Translation Model is free software: you can redistribute it and/or
modify it under the terms of the GNU General Public License as published
by the Free Software Foundation, either version 3 of the License, or (at
your option) any later version.

The RadSim Translation Model is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
Public License for more details.

You should have received a copy of the GNU General Public License along
with the RadSim Translation Model. If not, see <http://www.gnu.org/licenses/>.


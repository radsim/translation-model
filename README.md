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
    - [Release a new Version](#release-a-new-version)
    - [Publishing Artifacts to GitHub Packages
      manually](#publishing-artifacts-to-github-packages-manually)
- [License](#license)

-----------------------------------------------------------------------------

## Developer Guide

This section is only relevant for developers of this library.

The library uses [Gradle](https://gradle.org/) as the build system

### Release a new Version

See [Cyface Collector
Readme](https://github.com/cyface-de/data-collector#release-a-new-version)

- `version` in root `build.gradle.kts` is automatically set by the CI
- Just tag the release and push the tag to GitHub
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


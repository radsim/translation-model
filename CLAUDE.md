# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

This is the **RadSim Translation Model** library - a bidirectional mapping library for converting between OpenStreetMap (OSM) tags and RadSim tags for bicycle infrastructure. The library is published to GitHub Packages and used by the RadSim backend system.

**Key Concept**: The library handles two distinct tag formats:
- **OSM tags**: Real-world OpenStreetMap data (e.g., `highway=cycleway`, `bicycle=designated`)
- **RadSim tags**: Simplified simulation tags (e.g., `roadStyle=BicycleWay`, `surface=Comfort1Asphalt`)

## Core Architecture

### Tag Translation Flow

The library implements hierarchical bidirectional mapping:

1. **OSM → RadSim (Forward Mapping)**:
   - Entry point: `ToRadSimMapper.kt:ToRadSimMapper.map()`
   - Delegates to enum-specific `toRadSim()` methods:
     - `BikeInfrastructure.toRadSim()` - Complex hierarchical bicycle infrastructure classification
     - `SurfaceType.toRadSim()` - Surface quality categorization
     - `Speed.toRadSim()` - Speed limit mapping

2. **RadSim → OSM (Back-Mapping)**:
   - Entry point: `RadSimDeltaEngine.kt:computeDelta()`
   - Returns delta tags (add/remove) to transform current OSM tags
   - For infrastructure: Uses rule-based matrix approach via `BackMappingRules.kt`
   - Handles category transitions recursively (e.g., NO → BICYCLE_ROAD → BICYCLE_WAY)

### Two Infrastructure Representations

The codebase maintains two bike infrastructure models:

1. **`BikeInfrastructure`** (Detailed, 40+ values):
   - Represents OSM's detailed left/right infrastructure annotation
   - Examples: `BICYCLE_WAY_RIGHT_LANE_LEFT`, `BICYCLE_LANE_BOTH`
   - Used for OSM → RadSim mapping
   - Each enum has a `.simplified` property

2. **`SimplifiedBikeInfrastructure`** (7 categories):
   - Client-facing simplified categories: `CYCLE_HIGHWAY`, `BICYCLE_ROAD`, `BICYCLE_WAY`, `BICYCLE_LANE`, `BUS_LANE`, `MIXED_WAY`, `NO`
   - Used for RadSim → OSM back-mapping
   - RadSim tag: `roadStyleSimplified`

### Back-Mapping Rule Engine

**`BackMappingRules.kt`** implements R1-R22 transformation rules from the official matrix (2025-08-21, BIK-1440):
- Maps transitions between `SimplifiedBikeInfrastructure` categories
- Context-aware: Examines current `highway` tags to determine appropriate transformations
- Returns delta tags (tags to add/remove/modify)

**`RadSimDeltaEngine.kt`** applies rules recursively:
- Computes multi-step transitions (e.g., NO → BICYCLE_WAY may route through BICYCLE_ROAD)
- Validates transitions actually change categories (guards against stalls)
- Cycle detection prevents infinite loops

### Tag Format Validation

**`TagFormatValidator.kt`** (BIK-1478):
- Ensures `toRadSim()` functions receive OSM tags, NOT RadSim tags
- Distinguishes three formats by key presence:
  - OSM format: `highway`, `cycleway`, `bicycle`
  - RadSim Simplified: `roadStyleSimplified`
  - RadSim Full: `roadStyle`
- Throws `IllegalArgumentException` with detailed diagnostics if wrong format detected

## Build and Test Commands

### Build
```bash
./gradlew build
```

### Run Tests
```bash
# Run all tests
./gradlew test

# Run specific test class
./gradlew test --tests "BikeInfrastructureTest"

# Run specific test method
./gradlew test --tests "BikeInfrastructureTest.testToRadSim"
```

### Linting
```bash
# Run detekt static analysis
./gradlew detekt
```

### Documentation
```bash
# Generate Dokka documentation
./gradlew dokkaHtml
# Output: build/doc/
```

### Publishing
```bash
# Publish to GitHub Packages (requires credentials in gradle.properties)
./gradlew publish
```

## Development Guidelines

### Testing Approach
- Parameterized tests using JUnit 5 (`@ParameterizedTest` + `@MethodSource`)
- Test data as companion object streams
- Hamcrest matchers for assertions
- Mark bug-specific test cases with ticket IDs (e.g., `[BIK-1478]`)

### Code Validation
All `toRadSim()` functions must call `TagFormatValidator.requireOsmFormat()` at entry to prevent accidental RadSim tag input.

### Hierarchical Mapping Logic
`BikeInfrastructure.toRadSim()` checks tags hierarchically (see line 260+):
1. Service/accessibility checks
2. Cycle highway
3. Bicycle road
4. Right-side infrastructure (bicycle way → lane → bus → mixed → MIT)
5. Left-side infrastructure
6. Fallback to `NO`

Order matters - higher-priority infrastructure is checked first.

### Adding New Back-Mapping Rules
1. Add rule to `BackMappingRules.rules` map with `RuleKey(from, to)`
2. Return delta as `Set<OsmTag>` (empty string values = remove tag)
3. Test that applying delta + calling `BikeInfrastructure.toRadSim()` yields target category
4. Add test case to `BackMappingRulesTest.kt` or `BackMappingMatrixTest.kt`

## File Structure

```
src/main/kotlin/de/radsim/translation/model/
├── ToRadSimMapper.kt              # OSM → RadSim entry point
├── RadSimDeltaEngine.kt           # RadSim → OSM entry point
├── BikeInfrastructure.kt          # Detailed infrastructure (40+ types)
├── SimplifiedBikeInfrastructure.kt # Simplified infrastructure (7 types)
├── BackMappingRules.kt            # R1-R22 transformation rules
├── TagFormatValidator.kt          # Format validation guard
├── SurfaceType.kt                 # Surface quality mapping
├── Speed.kt                       # Speed limit mapping
├── Mapping.kt                     # Generic mapping helper
├── WayId.kt                       # Way identifier
└── Crossing.kt                    # Crossing representation

src/test/kotlin/de/radsim/translation/model/
├── BikeInfrastructureTest.kt
├── SimplifiedBikeInfrastructureTest.kt
├── BackMappingRulesTest.kt
├── BackMappingMatrixTest.kt
├── SurfaceTypeTest.kt
└── SpeedTest.kt
```

## Common Pitfalls

1. **Passing RadSim tags to `toRadSim()` functions**: Always use OSM tags. The validator will catch this.

2. **Empty delta interpretation**: Empty-value tags (`OsmTag("key", "")`) mean "remove this tag", not "set to empty string".

3. **Back-mapping cycles**: If adding a rule, ensure it makes forward progress. The delta engine validates this.

4. **Highway tag assumptions**: Some rules check/modify `highway` tag. Be aware NO category should never have `highway=cycleway` (BIK-1440).

5. **Left/right vs forward/backward**: Current implementation uses left/right. Future work will consider forward/backward, one-way, and geometry direction (see `SimplifiedBikeInfrastructure.kt` comments).

## Dependencies

- **Kotlin 2.1.20** with JVM toolchain 17
- **Cyface Serializer 4.1.11** - Provides `OsmTag` class
- **JUnit 5** for testing
- **Hamcrest** for assertions
- **Detekt** for static analysis
- **Dokka** for documentation generation

## Release Process

Releases are automated via GitHub Actions:
1. Version in `build.gradle.kts` is auto-managed by CI
2. Tag the release: `git tag v2.3.4 && git push origin v2.3.4`
3. GitHub Actions automatically publishes to GitHub Packages
4. Tag is marked as GitHub Release

See README.md for manual publishing instructions.

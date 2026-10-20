# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

The README is the main documentation. It describes the mapping in both directions, the infrastructure models, the development commands, the workflows and the release:

@README.md

## Rules for Changes

- Keep the README current. When you change a mapping, a back-mapping rule, a command or a workflow, update the README in the same commit.
- Each `toRadSim()` function must call `TagFormatValidator.requireOsmFormat()` first.
- A new back-mapping rule must change the category. Test it with `BikeInfrastructure.toRadSim()`.
- Run `./gradlew detekt` before each commit.
- Follow the rules of the workflows in the README section "GitHub Workflows" for each change of `.github/workflows/`.

## Commit Style

`[TICKET-ID] Short imperative summary`, for example `[BIK-####]` or `[CY-####]`.

# Prisma Launcher Architecture

## Goal

Build an independent Geometry Dash launcher and mod platform. Geode and Noahh are references for user experience and feature expectations only; neither is a runtime dependency or a source of copied implementation.

## Proposed components

1. **Launcher UI** — home screen, game/version selection, settings, status and diagnostics.
2. **Game discovery** — detect supported Geometry Dash installations and report their versions.
3. **Mod catalog** — display available mods and compatibility metadata.
4. **Package manager** — download, validate, install, enable, disable, update and remove mod packages.
5. **Runtime integration** — an independently implemented and version-aware mechanism that allows supported mods to run in the game.
6. **Storage and configuration** — keep launcher data, mod files, settings and logs organized.
7. **Compatibility layer** — isolate Android- and game-version-specific behavior.

## Non-negotiable constraints

- Do not require Geode or Noahh to be installed.
- Do not copy their proprietary code or assets.
- Do not claim native mod loading works until the runtime integration is implemented and tested.
- Validate package integrity and compatibility before installation.
- Keep user data and game files safe; avoid modifying unrelated files.

## Decisions still open

- Primary implementation language and UI framework
- Minimum Android version and supported device architectures
- Supported Geometry Dash versions
- Mod package format and manifest schema
- Runtime integration strategy and build pipeline
- Distribution and update mechanism

These decisions should be made before creating platform-specific implementation files.

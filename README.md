# Prisma Launcher

An independent launcher and mod-management platform for Geometry Dash, inspired by the user experience of Geode.

> **Project status:** Early development. This repository is the home of Prisma Launcher's own architecture and implementation.

## Project principles

- **Independent:** no runtime dependency on Geode or Noahh.
- **Original implementation:** familiar workflows, but Prisma-owned code and systems.
- **Modular:** separate launcher UI, game integration, mod discovery, installation, and configuration.
- **Transparent:** document compatibility, permissions, and limitations clearly.

## Planned capabilities

- Launcher interface and game installation detection
- Mod browser and local mod manager
- Mod installation, enable/disable, and removal
- Version and compatibility management
- Settings, logs, and diagnostics
- A safe, documented extension format

## Important technical note

A launcher UI alone cannot load native mods into Geometry Dash. That requires a compatible game-integration/loader layer, built and maintained independently. The project will establish that layer rather than embedding or depending on Geode or Noahh. Compatibility must be validated against the target Geometry Dash version and Android environment.

## Repository layout

```text
Prisma-Launcher/
├── README.md
├── .gitignore
└── docs/
    ├── ARCHITECTURE.md
    └── ROADMAP.md
```

## Development

The implementation stack and Android packaging approach will be selected before adding platform-specific code. See [Architecture](docs/ARCHITECTURE.md) and [Roadmap](docs/ROADMAP.md).

## Disclaimer

Prisma Launcher is an independent community project and is not affiliated with, endorsed by, or sponsored by RobTop Games, Geode, or Noahh. Geometry Dash and related trademarks belong to their respective owners.

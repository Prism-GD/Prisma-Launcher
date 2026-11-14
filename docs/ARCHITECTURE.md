# Prisma Launcher Architecture

## Goal

Build an independent Geometry Dash launcher and in-game mod platform. Geode and Noahh are references for user experience and feature expectations only; neither is a runtime dependency or a source of copied implementation.

## Components

1. **External launcher UI** — game detection, launch action, settings and diagnostics. It is not the main mod-management interface.
2. **Game discovery** — detect supported Geometry Dash installations and report versions and device compatibility.
3. **In-game Prisma orb and panel** — a small floating control near the lower screen edge opens the mod window inside Geometry Dash.
4. **In-game mod UI** — installed mods, enable/disable, per-mod settings, package details, browser and logs.
5. **Package manager** — validate, install, enable, disable, update and remove `.prism` packages.
6. **Runtime integration** — an independently implemented, version-aware mechanism for supported game builds, including rendering and input integration for the in-game UI.
7. **Storage and compatibility** — mod files, configuration, logs and Android/game-version-specific behavior.

## Non-negotiable constraints

- Do not require Geode or Noahh to be installed.
- Do not copy their proprietary code or assets.
- Do not claim the orb, in-game panel or native mod loading works until the runtime integration is implemented and tested.
- A normal Android Activity or system overlay is not equivalent to UI rendered inside the game's own process/surface.
- Validate package integrity and compatibility before installation.
- Keep user data and the original game installation safe.

## Implementation direction

The launcher home remains a separate Android screen. Mod management is intended to happen after the game starts, through the Prisma orb and panel inside Geometry Dash. This requires an independently developed game integration; it cannot be achieved by Compose UI alone.

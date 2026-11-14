# Prisma Launcher — Home screen

The Compose implementation is in `app/src/main/java/dev/prisma/launcher/ui/LauncherScreen.kt`.

## Included
- Prisma branding and dark purple visual theme.
- Geometry Dash installation status from `InstalledGameDetector`.
- Version name/code and ARM64 capability display.
- Play button enabled only when the game is installed, enabled, and launchable.
- Settings entry point.

## Product decision

The launcher home is only for detecting and starting Geometry Dash. Mod management is not a separate page here: the intended Mods interface is an in-game panel opened by tapping the Prisma orb near the lower corner/edge of the game screen. See `docs/IN_GAME_OVERLAY.md`.

## Build status

This source uses Jetpack Compose Material 3. The repository still needs a complete Android Gradle project and dependency configuration before it can be compiled or installed. The in-game orb/panel and game runtime integration are not implemented yet.

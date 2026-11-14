# Prisma Launcher — Home screen

The first Compose home-screen implementation is in
`app/src/main/java/dev/prisma/launcher/ui/LauncherScreen.kt`.

## Included
- Prisma branding and dark purple visual theme.
- Geometry Dash installation status from `InstalledGameDetector`.
- Version name/code and ARM64 capability display.
- Play button enabled only when the game is installed, enabled, and launchable.
- Navigation callbacks for Mods and Settings.
- Preview status label; no claim that mod loading is implemented.

## Host integration

Create an `InstalledGameDetector` with an Android Context and call
`LauncherScreen(detector, onPlay, onMods, onSettings)` from the app's Compose
content. The host Activity must implement the callbacks and launch the detected
game through Android's package launch intent. The screen itself does not clone,
patch, modify, or inject into Geometry Dash.

## Build status

This source uses Jetpack Compose Material 3. The repository still needs a
complete Android Gradle project and dependency configuration before it can be
compiled or installed.

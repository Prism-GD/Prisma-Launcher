# Android host architecture research (Geometry Dash 2.2091)

Research date: 2026-11-13/14
Target: Android ARM64, Geometry Dash package `com.robtopx.geometryjump`, version label 2.2091 (version code 43).

## Important correction: how the Geode Android launcher starts the game

The official `geode-sdk/android-launcher` source shows a different model from a normal Android launcher:

- It does **not** call the Geometry Dash package's launch intent for the modded session.
- It opens its own `GeometryDashActivity` and creates a Cocos2d OpenGL surface in that activity.
- It reads the installed game's `PackageInfo`, APK path, split APK paths and native-library directory.
- It adds the game's APK/split APK asset paths to its own `AssetManager`.
- It initializes the Cocos2d Android helper, then loads the game's FMOD and Cocos native libraries into the launcher's own process.
- It performs launcher-specific native compatibility/data-path setup, then loads its loader library.
- The Geometry Dash native code therefore runs in the launcher's host Activity/process; it is not a library injected into the separately running Geometry Dash app process.

Primary public source files reviewed:

- `app/src/main/java/com/geode/launcher/GeometryDashActivity.kt`
- `app/src/main/java/com/geode/launcher/utils/GamePackageUtils.kt`
- `app/src/main/java/com/geode/launcher/utils/LaunchUtils.kt`
- `app/src/main/cpp/launcher-fix.cpp`
- `app/src/main/cpp/patcher.cpp`

Official repository: https://github.com/geode-sdk/android-launcher

The README describes the product as launching a vanilla Geometry Dash copy with the loader added. The implementation confirms that this is a host-activity architecture. This research records behavior and architecture only; Prisma must implement its own code and must not copy Geode implementation code or depend on Geode binaries.

## What this means for Prisma

A standalone Android launcher can provide an in-game experience without injecting into the original app process by becoming the host for the game engine itself. Prisma would launch its own game-host Activity, initialize a Cocos2d GL surface, load the installed game's compatible native libraries in the Prisma process, and load the original game's assets from its installed APK/splits.

This is materially different from the current Prisma button, which starts the original Geometry Dash Activity using `getLaunchIntentForPackage()`.

## Version and compatibility notes

The Geode Android source maps Geometry Dash version code 43 to version 2.2091. It also contains explicit handling for split APKs, extracted vs non-extracted native libraries, ABI mismatch, and Samsung library namespace restrictions. These are important compatibility cases for a Prisma implementation.

The Geode source's current supported-version constants are not evidence that every 2.2091 build is compatible with Prisma. Prisma must validate the actual installed package, ABI, ELF dependencies and Cocos/JNI contract on the target device.

## Prisma implementation plan (original implementation)

1. **Host Activity skeleton:** add a dedicated Prisma game-host Activity and lifecycle handling. Keep the current external-launch button as a temporary fallback until the host is verified.
2. **Cocos Android bridge:** provide the required Android Cocos2d Java/GL surface integration in Prisma, with licensing and source provenance documented.
3. **Game package reader:** discover the installed game's base APK, split APKs, ABI-specific native libraries and asset paths without modifying the installed package.
4. **Native load preflight:** verify package version code 43, ARM64 compatibility, required libraries and dependencies before attempting any load.
5. **Native host bootstrap:** load FMOD and Cocos libraries in the correct host process/order, initialize Cocos, and report precise failures. Do not treat successful `System.load` alone as a successful game boot.
6. **Prisma runtime:** load Prisma's own native runtime after the host engine is ready, then establish a verified Cocos scene/lifecycle attachment point.
7. **Device validation:** test startup, pause/resume, audio, storage/save paths, touch, keyboard, rotation, crashes and the in-game orb on the Samsung A04s.

## Known risks and unresolved work

- The exact Android 2.2091 APK has not yet been inspected in this project. There is no recorded SHA-256, ELF dependency inventory, exported-symbol inventory or validated Cocos ABI.
- Cocos2d Java glue and RobTop-specific JNI expectations must match the target game build.
- Android linker namespace restrictions may prevent direct loading from the installed package's native library directory; the host must diagnose this instead of assuming one path works.
- The game's hard-coded storage paths may need a carefully tested Prisma-owned data-path strategy. Saves must never be overwritten or redirected without explicit validation and backup.
- A native library loading successfully does not prove the first game frame renders or that the game is stable.

## Current status

- [x] Confirmed the official Geode Android launcher uses its own host Activity and loads the game's native engine libraries in its own process.
- [x] Confirmed this is not equivalent to launching the original Geometry Dash Activity.
- [ ] Prisma host Activity implemented.
- [ ] Cocos Android bridge integrated and licensed.
- [ ] Installed-game native/asset reader implemented.
- [ ] Geometry Dash 2.2091 native load validated on device.
- [ ] Prisma runtime attached to an actual in-game Cocos scene.
- [ ] In-game overlay tested on device.

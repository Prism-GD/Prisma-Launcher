# Prisma micro-prototype

## Purpose

Validate the first in-game overlay UI independently from Geometry Dash:

- purple orb near the lower-right corner;
- open/close behavior;
- Mods, Browse, Settings and Logs tabs;
- sample installed-mod rows;
- touch routing and panel dismissal.

## Host scene

The optional Cocos2d-x v3 target exposes
`prisma::createCocos2dPrototypeScene()`. A small Cocos2d-x Android host app
can set this scene as its first scene to test the overlay on a device.

The exported host contract `prisma_attach_overlay_to_scene(Scene*)` is for a
future verified in-process adapter. It must be called on the Cocos2d main
thread from inside a compatible Cocos2d-x process.

## Build

```sh
cmake -S native/prisma_runtime -B build \
  -DPRISMA_WITH_COCOS2D=ON \
  -DCOCOS2D_INCLUDE_DIR=/path/to/cocos2d
cmake --build build
```

The embedding Android app must link the matching Cocos2d-x libraries and the
Prisma native target. Exact Cocos2d revision, NDK, ABI and Android toolchain
must match the host.

## Not yet integrated into Geometry Dash

This scene is a standalone UI validation harness. It does not attach itself
to the commercial Geometry Dash process. The public project does not contain
the game's Cocos2d headers, libraries or source, and no verified host call site
is currently available. The Geometry Dash integration milestone remains open.


## Android launcher JNI bridge

The Android app now has a CMake entry point at `app/src/main/cpp` and a Kotlin wrapper at `app/src/main/java/dev/prisma/launcher/runtime/NativeOverlayRuntime.kt`. The bridge owns an instance of the portable overlay state and exposes panel state and tab selection to Kotlin.

This milestone is source-integrated into the Android application. It does not attach to Geometry Dash or draw an in-game overlay. See [Android JNI integration](ANDROID_JNI_INTEGRATION.md) for the current boundary and build instructions.

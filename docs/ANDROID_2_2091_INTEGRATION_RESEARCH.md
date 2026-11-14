# Android 2.2091 in-process integration research

Research date: 2026-11-13/14
Target reported by the project owner: Geometry Dash 2.2091, Android ARM64.
Target package used by the launcher: com.robtopx.geometryjump.

## Finding

The repository currently has no verified way to obtain the active Geometry Dash
Cocos2d scene from the independent Prisma Launcher process.

The existing `prisma_attach_overlay_to_scene(Scene*)` API is only a host-side
attachment contract. It requires a valid `cocos2d::Scene*` from inside the
game process and must be called on the game's Cocos2d main thread. It does not
discover the scene, load the library into Geometry Dash, or cross Android's
application boundary.

Android's application sandbox assigns apps distinct UIDs and isolates their
processes. A launcher calling `startActivity()` starts the game; it does not
cause the game's process to load the launcher's native libraries. The Android
ART TI documentation also says runtime agent attachment is restricted to
debuggable apps and is not applicable to normal apps. Therefore a normal
third-party launcher cannot attach this C++ library to the production
Geometry Dash process through a supported public launcher API.

## Evidence and references

- Android Application Sandbox:
  https://source.android.google.cn/docs/security/app-sandbox
  Describes per-app UID and process isolation.
- Android ART TI:
  https://source.android.com/docs/core/runtime/art-ti
  Documents runtime agent attachment restrictions, including the debuggable
  requirement and the note that this is not applicable to normal apps.
- Android JNI / native library loading:
  https://developer.android.com/ndk/guides/jni-tips
  Documents `System.loadLibrary` as loading native code into the calling
  application's runtime, not another application's process.
- Geometry Dash 2.2091 version confirmation:
  https://apps.apple.com/ec/app/geometry-dash/id625334537
  Lists the 2.2091 update dated October 19. This confirms the version label,
  not Android binary internals or Cocos2d ABI.

## Binary evidence status

No Geometry Dash APK file was attached to the project conversation or found in
the user's Library during this investigation. Earlier notes only say the owner
downloaded an APK. There is no verified APK hash, ELF inventory, exported
symbol list, Cocos2d build/version string, scene lifecycle address, or
Android 2.2091 binary inspection in the repository.

The Cocos2d-x 3.17.2 desktop CI build proves only that the prototype compiles
against those public desktop headers. It does not prove ABI compatibility with
RobTop's Android game build.

## Decision under current project constraints

Prisma remains an independent launcher and must not claim in-process
integration until a supported host-side entry point is established. The
current project constraints exclude depending on Geode, copying its
implementation, injecting into another app process, cloning/repackaging the
game, or modifying the Geometry Dash APK. Under those constraints, there is
currently no verified route for a separate launcher to make its native layer
run inside the unmodified production game.

Do not fabricate function addresses, Cocos2d versions, or scene pointers.
Do not add speculative hooks or loader code and call it integration.

## What can be validated next

1. Obtain an authorized, locally available copy of the exact Android 2.2091
   APK only if binary inspection is permitted for this project; record its
   SHA-256 and package metadata without publishing the APK.
2. Inspect its native ELF inventory and exported symbols to determine what
   engine libraries are present. This alone will not establish a safe scene
   entry point.
3. Establish a supported extension/host mechanism that can execute in the
   game's own process without violating the project's constraints. If none
   exists, document the product/architecture limitation instead of pretending
   the standalone launcher can attach a Cocos2d layer to another process.

## Current status

- Portable overlay state: implemented and host-tested.
- JNI bridge: built inside Prisma's own Android process.
- Cocos2d prototype layer: compiled against desktop Cocos2d-x 3.17.2.
- Geometry Dash 2.2091 active-scene acquisition: not found / not verified.
- In-process bootstrap for the unmodified game: not implemented.
- In-game orb/panel on the target device: not implemented or tested.

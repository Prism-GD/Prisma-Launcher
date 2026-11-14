# Prisma in-game overlay

## Product behavior

Mod management belongs inside Geometry Dash, not in a separate launcher Mods page and not in an Android system overlay.

1. Start the supported game through Prisma.
2. A version-matched in-process host integration attaches the Prisma Cocos2d layer to the active game scene.
3. The layer draws the orb and panel using the game's own Cocos2d renderer.
4. Tapping the orb opens Mods, Browse, Settings and Logs.
5. Closing the panel returns input to the game.

## Native host contract

The optional Cocos2d adapter exposes a narrow C ABI in
prisma/cocos2d_host_api.hpp:
- prisma_cocos_host_api_version() reports the host contract version.
- prisma_attach_overlay_to_scene(scene) attaches the layer once per scene.
- prisma_detach_overlay_from_scene(scene) removes it safely.

The host must call these functions from inside the game process, on the Cocos2d
main thread, with a scene pointer from the exact compatible Cocos2d ABI. Attach
is idempotent, so repeated callbacks do not add duplicate layers.

This API is an attachment contract, not a loader. It does not discover a scene,
enter another process, patch the game, or create an in-process entry point.

## Android implementation constraint

A normal Android launcher Activity cannot draw UI inside another app's process
or Cocos2d surface merely by launching it. Android overlay permission creates
a system-level window and is not the intended implementation.

The intended end state is an independently implemented, version-aware
in-process integration for supported Geometry Dash builds. Cocos2d ABI, scene
lifecycle, render thread, input dispatch and game version must be verified on
device. A generic Cocos2d-x 3.17.2 desktop build is not evidence of binary
compatibility with the Android game.

## Current status

Implemented:
- portable C++ overlay state and tests;
- Android JNI bridge for the launcher's own process;
- Cocos2d-x v3 prototype layer;
- versioned, idempotent C ABI for a compatible in-process host to attach/detach.

Not implemented or verified:
- Android in-process bootstrap/loader for Geometry Dash;
- exact Cocos2d ABI used by the user's Geometry Dash 2.2091 build;
- verified active-scene acquisition point and scene lifecycle callback;
- routing game touch/render callbacks to the adapter;
- mod binary loading or execution.

Do not describe the overlay as running inside Geometry Dash until the host-side
pieces are implemented and tested on the target device. Geode is a UX reference
only; do not copy its code, assets, or proprietary implementation.

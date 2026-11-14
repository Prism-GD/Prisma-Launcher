# Prisma Cocos2d-x prototype

## What is in this prototype

`Cocos2dOverlayLayer` is an optional Cocos2d-x v3 scene layer. When a host
integration adds it to the active Geometry Dash scene, it draws:

- a purple Prisma orb near the lower-right edge;
- a panel with Mods, Browse, Settings and Logs tabs;
- an empty installed-mod state and basic mod rows;
- touch handling for opening/closing the panel and changing tabs.

## Build

This adapter is optional because the public repository does not contain
Geometry Dash's private/custom Cocos2d headers or libraries.

Configure with a compatible Cocos2d-x v3 SDK:

```sh
cmake -S native/prisma_runtime -B build \
  -DPRISMA_WITH_COCOS2D=ON \
  -DCOCOS2D_INCLUDE_DIR=/path/to/cocos2d
cmake --build build
```

The exact SDK revision and Android ABI must match the target game build.

## Host integration still required

The adapter is a scene layer, not an injector or loader. A verified host-side
entry point must create it and add it to the active scene on the Cocos2d main
thread. The supplied reference APK is a prebuilt commercial binary, and its
private headers/source are not part of this repository. Therefore this project
does not yet have a verified call site that can attach the layer to the running
game. The code must not be described as visible in Geometry Dash until that
call site is implemented and tested on the target build.

The Browse tab is currently a UI placeholder. The online catalog, package
signatures, download/install pipeline, and native mod loader are separate work.

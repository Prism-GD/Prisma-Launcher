# Prisma in-game overlay — runtime core

## Implemented in this stage

The portable C++17 overlay state core lives in `native/prisma_runtime/`.

- Orb/panel visibility state and toggle behavior.
- Four panel tabs: Mods, Browse, Settings and Logs.
- In-memory mod view models.
- Enable/disable state changes.
- Incompatible mods cannot be enabled through this state API.
- Duplicate or empty mod IDs are rejected when replacing the list.
- A small CMake test executable covers the state transitions.

## Build and test

From this directory:

```sh
cmake -S . -B build -DPRISMA_BUILD_TESTS=ON
cmake --build build
ctest --test-dir build --output-on-failure
```

## Important boundary

This is the UI state/model layer only. It does not draw the orb or panel, intercept game input, inject into Geometry Dash, load native mods, or modify the installed game. The next integration layer must provide a supported in-process renderer/input adapter for explicitly supported game builds. Do not use an Android system overlay as a substitute and do not claim the in-game UI is functional until tested in the game.

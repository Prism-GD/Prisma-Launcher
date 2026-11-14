# Geometry Dash in-process render bridge

## First implementation

The native runtime now has an Android OpenGL ES 2 renderer and a touch dispatcher:

- `GameOverlayRenderer` draws a first-pass Prisma panel and purple orb.
- `GameOverlayInput` routes touch-up coordinates to the orb and tab state.
- Rendering and input are deliberately exposed as calls for a game-side adapter.

## Required call site

The integration adapter must call:

1. `GameOverlayRenderer::initialize()` after the Geometry Dash GL context is current.
2. `GameOverlayRenderer::draw(state, framebufferWidth, framebufferHeight)` from the game's GL render thread.
3. `GameOverlayInput::onTouchUp(x, y, width, height)` from the game's touch callback, before forwarding unconsumed touches to game controls.
4. `GameOverlayRenderer::shutdown()` while the GL context is still valid.

## Current limitation

This repository does **not** yet install a call site into Geometry Dash. The renderer is not automatically loaded by the game, and the input dispatcher is not connected to the game's touch callback. No APK patching, binary hooking, process injection, or game cloning is included. Until a supported integration adapter is implemented and tested against the exact game build, the orb will not appear in the installed game.

The current renderer is a visual proof-of-path: it draws simple shapes, not production text, icons, scrolling lists, mod cards, or the full Browse catalog. It also does not load mods. The Browse catalog needs a separate signed/indexed repository API and package download/verification flow.

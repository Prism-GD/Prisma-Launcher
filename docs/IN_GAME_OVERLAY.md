# Prisma in-game overlay

## Product behavior

Mod management belongs inside Geometry Dash, not on a separate launcher Mods page.

1. Start the supported game through Prisma.
2. Show a small draggable Prisma orb near the lower edge/corner of the game screen.
3. Tapping the orb toggles the Prisma panel.
4. The panel contains installed mods, enable/disable controls, per-mod settings, package details, and a route to the mod browser.
5. Closing the panel returns input and rendering to the game.

## Initial panel structure

- Header: Prisma mark, title, close button.
- Tabs: Mods, Browse, Settings, Logs.
- Mods: installed package list, enabled state, version and compatibility.
- Mod details: description, author, permissions, settings and disable/remove actions.
- Browse: discover/import `.prism` packages.
- Logs: runtime and mod diagnostics.

## Android implementation constraint

A normal Android launcher Activity cannot draw arbitrary UI inside another app's process or Cocos2d surface merely by launching it. Android overlay permission (`SYSTEM_ALERT_WINDOW`) would create a system-level window, not a true in-game Cocos2d UI, and can be unreliable or inappropriate for touch/game rendering.

The intended end state is an independently implemented, version-aware in-process integration for supported Geometry Dash builds, with a native rendering/input bridge for the orb and panel. This is a substantial runtime task and must be validated on-device. Do not claim the orb or panel works until that integration exists. Geode is a UX/behavior reference only: do not copy its code, assets, or proprietary implementation.

## Current status

This document records the product decision. The existing Compose launcher screen is only the external launcher home; it is not the in-game overlay. Runtime integration, orb rendering, panel rendering, touch routing, and mod execution are not implemented yet.

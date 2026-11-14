# Geometry Dash APK reference

This document records the APK supplied in the Prisma Launcher development conversation for later compatibility analysis.

## Handling

- The APK itself is **not stored in this public repository**. It is a commercial game package and publishing it here could redistribute copyrighted game assets.
- The supplied attachment is available for analysis in the current conversation, but should not be treated as permanent project storage.
- Do not commit the APK, extracted game assets, music, or proprietary binaries to this repository.
- The filename supplied by the user is `Geometry Dash 2.2091 Android (64 bits).apk`. The version label is user-provided and has not yet been independently verified from the package metadata.

## Observed archive facts

| Property | Observed value |
|---|---:|
| File size | 174,650,508 bytes (about 166.5 MiB) |
| SHA-256 | `780eb29421c9eb4eee068ca195b132b8fe96a7473342fe67e23e7f4a6e245fb0` |
| Archive entries | 6,185 |
| Total uncompressed entry size | 197,520,474 bytes |
| Total compressed entry size | 173,689,585 bytes |
| Native ABI directory | `arm64-v8a` |
| Native libraries observed | `lib/arm64-v8a/libcocos2dcpp.so`, `lib/arm64-v8a/libfmod.so` |

The archive contains `classes.dex`, `AndroidManifest.xml`, `resources.arsc`, and game assets including audio, levels, icons, and sound effects.

## Analysis status

This is an initial ZIP/APK inventory only. Package name, version code, signing certificate, minimum Android version, and runtime behavior have not yet been verified. No files have been extracted into the project and nothing from this APK has been executed.

## Next analysis steps

1. Decode the binary Android manifest and record package/version/SDK metadata.
2. Inspect APK signing information and native library metadata.
3. Map the app startup path and Cocos2d native entry points at a high level.
4. Use findings to design Prisma's own independent launch and integration flow; do not copy proprietary implementation code.

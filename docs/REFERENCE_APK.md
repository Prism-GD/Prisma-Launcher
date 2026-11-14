# Geometry Dash APK reference

This document records the APK supplied in the Prisma Launcher development conversation for later compatibility analysis.

## Handling

- The APK itself is **not stored in this public repository**. It is a commercial game package and publishing it here could redistribute copyrighted game assets.
- The supplied attachment is available for analysis in the current conversation, but should not be treated as permanent project storage.
- Do not commit the APK, extracted game assets, music, or proprietary binaries to this repository.
- The filename supplied by the user is `Geometry Dash 2.2091 Android (64 bits).apk`, but the decoded manifest reports version `2.2.145`. Treat the manifest value as the observed package metadata; the filename label may be inaccurate.

## Observed archive facts

| Property | Observed value |
|---|---:|
| File size | 174,650,508 bytes (about 166.5 MiB) |
| SHA-256 | `780eb29421c9eb4eee068ca195b132b8fe96a7473342fe67e23e7f4a6e245fb0` |
| Archive entries | 6,185 |
| Total uncompressed entry size | 197,520,474 bytes |
| Total compressed entry size | 173,689,585 bytes |
| Package | `com.robtopx.geometryjump` |
| Manifest version name | `2.2.145` |
| Manifest version code | `43` |
| Minimum SDK | `23` (Android 6.0) |
| Target SDK | `36` |
| Native ABI directory | `arm64-v8a` |
| Native libraries observed | `lib/arm64-v8a/libcocos2dcpp.so`, `lib/arm64-v8a/libfmod.so` |

## Android entry points and native runtime

The decoded manifest declares:

- Launcher activity: `com.robtopx.geometryjump.GeometryJump`
- Activity is exported and uses `singleTask` launch mode.
- Main launcher intent: `android.intent.action.MAIN` + `android.intent.category.LAUNCHER`.
- Application native library metadata: `android.app.lib_name = cocos2dcpp`.
- Required OpenGL ES feature: version `0x20000` (OpenGL ES 2.0).
- Permissions include Internet, network state, Wi-Fi state, phone state, and legacy external storage write access.

ELF inspection of `libcocos2dcpp.so` confirms it is a 64-bit AArch64 shared object. Its dynamic dependencies include `libfmod.so`, `libGLESv2.so`, `liblog.so`, `libz.so`, `libdl.so`, `libstdc++.so`, `libm.so`, and `libc.so`.

The native library exports Cocos2d JNI entry points including `Java_org_cocos2dx_lib_Cocos2dxRenderer_nativeInit`, `nativeRender`, `nativeOnResume`, and `nativeOnSurfaceChanged`. It also exports bridge methods under `Java_com_customRobTop_JniToCpp_*`.

## Analysis status and limits

This is static archive, manifest, and ELF metadata inspection only. The APK has not been installed or executed. The signing certificate and runtime behavior have not yet been verified. The exported JNI symbols identify integration boundaries, but do not by themselves establish a safe or supported method for injecting code or cloning the game.

## Prisma design implications

1. Prisma should detect the installed package by package name and inspect its version and ABI before offering compatibility.
2. The native game runtime is Cocos2d-based; an Android-side launcher UI should remain separate from the game's native rendering layer.
3. The manifest activity and Cocos2d JNI initialization are useful high-level reference points for understanding startup. Prisma must implement its own launcher/integration code rather than copying proprietary code.
4. A separate copied installation is not assumed to be possible: Android package signing, app sandboxing, Play services, and scoped storage can constrain cloning. Any design must be validated against Android's actual installation model.
5. The supplied APK is arm64-only based on the native library directory observed in this archive.

## Next analysis steps

1. Verify APK signing certificate and package signing scheme.
2. Inspect the Java/Kotlin DEX activity at a high level to map its calls into the Cocos2d native layer.
3. Design a lawful, non-destructive installed-game detection and launch flow for Prisma.

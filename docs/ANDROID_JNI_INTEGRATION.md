# Android JNI integration

## Implemented

The Android source tree now contains a JNI bridge for the portable Prisma overlay state:

- `app/src/main/cpp/CMakeLists.txt` adds `native/prisma_runtime` as a CMake subproject.
- `prisma_launcher_jni` exports JNI methods used by `NativeOverlayRuntime.kt`.
- The Kotlin wrapper owns and releases the native `OverlayState` instance.
- The Android ARM64 CI job builds both shared libraries and uploads them as the `prisma-android-arm64-runtime` artifact.

The wrapper exposes panel open/close/toggle, orb visibility, panel state, and the four tab selections. This is a real Kotlin-to-C++ bridge for the state model.

## Build

The Android native build uses the NDK toolchain and the app CMake entry point:

```sh
cmake -S app/src/main/cpp -B build-android-arm64 \
  -DCMAKE_TOOLCHAIN_FILE="$ANDROID_NDK_HOME/build/cmake/android.toolchain.cmake" \
  -DANDROID_ABI=arm64-v8a \
  -DANDROID_PLATFORM=android-23 \
  -DANDROID_STL=c++_static \
  -DPRISMA_NATIVE_ROOT="$PWD/native/prisma_runtime"
cmake --build build-android-arm64 --parallel
```

The build produces `libprisma_launcher_jni.so` and `libprisma_overlay_state.so`.

## Not yet integrated

- The repository does not yet contain a complete Android Gradle application setup, manifest, or APK packaging task. Therefore these libraries are not yet packaged into an installable Prisma APK.
- The bridge controls the native state model only; it does not draw an overlay in Geometry Dash.
- No supported Geometry Dash in-process host entry point, render callback, or input callback has been implemented or validated.
- The JNI bridge does not inject into, patch, or modify Geometry Dash.

Do not describe this milestone as a working in-game overlay. The next safe milestone is to complete the Android app packaging/build pipeline, then separately validate an authorized in-process integration contract for supported game builds.

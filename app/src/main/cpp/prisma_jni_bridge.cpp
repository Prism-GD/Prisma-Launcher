#include <jni.h>

#include "prisma/overlay_state.hpp"

#include <cstdint>

namespace {

prisma::OverlayState* stateFrom(jlong handle) noexcept {
    return reinterpret_cast<prisma::OverlayState*>(static_cast<std::uintptr_t>(handle));
}

} // namespace

extern "C" JNIEXPORT jlong JNICALL
Java_dev_prisma_launcher_runtime_NativeOverlayRuntime_nativeCreate(JNIEnv*, jobject) {
    auto* state = new (std::nothrow) prisma::OverlayState();
    return static_cast<jlong>(reinterpret_cast<std::uintptr_t>(state));
}

extern "C" JNIEXPORT void JNICALL
Java_dev_prisma_launcher_runtime_NativeOverlayRuntime_nativeDestroy(JNIEnv*, jobject, jlong handle) {
    delete stateFrom(handle);
}

extern "C" JNIEXPORT void JNICALL
Java_dev_prisma_launcher_runtime_NativeOverlayRuntime_nativeOpenPanel(JNIEnv*, jobject, jlong handle) {
    if (auto* state = stateFrom(handle)) state->openPanel();
}

extern "C" JNIEXPORT void JNICALL
Java_dev_prisma_launcher_runtime_NativeOverlayRuntime_nativeClosePanel(JNIEnv*, jobject, jlong handle) {
    if (auto* state = stateFrom(handle)) state->closePanel();
}

extern "C" JNIEXPORT void JNICALL
Java_dev_prisma_launcher_runtime_NativeOverlayRuntime_nativeTogglePanel(JNIEnv*, jobject, jlong handle) {
    if (auto* state = stateFrom(handle)) state->toggleOrb();
}

extern "C" JNIEXPORT jboolean JNICALL
Java_dev_prisma_launcher_runtime_NativeOverlayRuntime_nativeIsPanelOpen(JNIEnv*, jobject, jlong handle) {
    const auto* state = stateFrom(handle);
    return state && state->isPanelOpen() ? JNI_TRUE : JNI_FALSE;
}

extern "C" JNIEXPORT jboolean JNICALL
Java_dev_prisma_launcher_runtime_NativeOverlayRuntime_nativeIsOrbVisible(JNIEnv*, jobject, jlong handle) {
    const auto* state = stateFrom(handle);
    return state && state->isOrbVisible() ? JNI_TRUE : JNI_FALSE;
}

extern "C" JNIEXPORT void JNICALL
Java_dev_prisma_launcher_runtime_NativeOverlayRuntime_nativeSelectTab(JNIEnv*, jobject, jlong handle, jint tab) {
    auto* state = stateFrom(handle);
    if (!state) return;

    switch (tab) {
        case 0: state->selectTab(prisma::OverlayTab::Mods); break;
        case 1: state->selectTab(prisma::OverlayTab::Browse); break;
        case 2: state->selectTab(prisma::OverlayTab::Settings); break;
        case 3: state->selectTab(prisma::OverlayTab::Logs); break;
        default: break;
    }
}

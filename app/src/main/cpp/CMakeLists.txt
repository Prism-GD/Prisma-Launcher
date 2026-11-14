cmake_minimum_required(VERSION 3.22.1)
project(prisma_launcher_jni LANGUAGES CXX)

# The Android app owns the JNI entry point and links the portable Prisma runtime.
# The runtime remains an independent CMake subproject shared with desktop tests.
set(PRISMA_BUILD_TESTS OFF CACHE BOOL "Disable host tests in the Android app build" FORCE)
set(PRISMA_NATIVE_ROOT "${CMAKE_CURRENT_LIST_DIR}/../../../../native/prisma_runtime"
    CACHE PATH "Path to Prisma's portable native runtime")

if(NOT EXISTS "${PRISMA_NATIVE_ROOT}/CMakeLists.txt")
    message(FATAL_ERROR "Prisma native runtime not found: ${PRISMA_NATIVE_ROOT}")
endif()

add_subdirectory("${PRISMA_NATIVE_ROOT}" prisma_runtime)

add_library(prisma_launcher_jni SHARED
    prisma_jni_bridge.cpp
)

target_compile_features(prisma_launcher_jni PRIVATE cxx_std_17)
target_link_libraries(prisma_launcher_jni PRIVATE prisma_overlay_state)

cmake_minimum_required(VERSION 3.16)
project(prisma_overlay_state LANGUAGES CXX)

add_library(prisma_overlay_state STATIC
    src/overlay_state.cpp
    src/game_overlay_renderer.cpp
    src/game_overlay_input.cpp
)

target_include_directories(prisma_overlay_state
    PUBLIC include
)

target_compile_features(prisma_overlay_state PUBLIC cxx_std_17)

option(PRISMA_WITH_COCOS2D "Build the optional Cocos2d-x scene adapter" OFF)
set(COCOS2D_INCLUDE_DIR "" CACHE PATH "Directory containing cocos2d.h")
if(PRISMA_WITH_COCOS2D)
    if(NOT COCOS2D_INCLUDE_DIR OR NOT EXISTS "${COCOS2D_INCLUDE_DIR}/cocos2d.h")
        message(FATAL_ERROR "Set COCOS2D_INCLUDE_DIR to a directory containing cocos2d.h")
    endif()
    target_sources(prisma_overlay_state PRIVATE
        src/cocos2d_overlay_layer.cpp
        src/cocos2d_host_api.cpp
        src/cocos2d_prototype_scene.cpp
    )
    target_include_directories(prisma_overlay_state PUBLIC
        ${COCOS2D_INCLUDE_DIR}
        ${COCOS2D_INCLUDE_DIR}/platform
        ${COCOS2D_INCLUDE_DIR}/platform/android
        ${COCOS2D_INCLUDE_DIR}/../../external
    )
    target_compile_definitions(prisma_overlay_state PUBLIC PRISMA_WITH_COCOS2D=1)
    # Cocos2d-x 3.17.2 includes glfw3.h without the GLFW/ prefix on Linux.
    # Debian/Ubuntu installs it under /usr/include/GLFW, so add that directory.
    find_path(PRISMA_GLFW_INCLUDE_DIR glfw3.h PATHS /usr/include/GLFW)
    if(PRISMA_GLFW_INCLUDE_DIR)
        target_include_directories(prisma_overlay_state PUBLIC ${PRISMA_GLFW_INCLUDE_DIR})
    endif()
    # Cocos2d-x 3.17.2 detects desktop Linux through the LINUX macro.
    # GitHub's Ubuntu runner does not define it automatically.
    if(CMAKE_SYSTEM_NAME STREQUAL "Linux" AND NOT ANDROID)
        target_compile_definitions(prisma_overlay_state PUBLIC LINUX=1)
    endif()
endif()

if(ANDROID)
    find_library(PRISMA_GLESV2_LIBRARY GLESv2)
    if(NOT PRISMA_GLESV2_LIBRARY)
        message(FATAL_ERROR "OpenGL ES 2 library (GLESv2) was not found")
    endif()
    target_link_libraries(prisma_overlay_state PUBLIC ${PRISMA_GLESV2_LIBRARY})
endif()

option(PRISMA_BUILD_TESTS "Build Prisma runtime state tests" ON)

if(PRISMA_BUILD_TESTS)
    enable_testing()
    add_executable(prisma_overlay_state_test
        tests/overlay_state_test.cpp
    )
    target_link_libraries(prisma_overlay_state_test PRIVATE prisma_overlay_state)
    add_test(NAME prisma_overlay_state_test COMMAND prisma_overlay_state_test)
endif()

cmake_minimum_required(VERSION 3.16)
project(prisma_overlay_state LANGUAGES CXX)

add_library(prisma_overlay_state STATIC
    src/overlay_state.cpp
)

target_include_directories(prisma_overlay_state
    PUBLIC include
)

target_compile_features(prisma_overlay_state PUBLIC cxx_std_17)

option(PRISMA_BUILD_TESTS "Build Prisma runtime state tests" ON)

if(PRISMA_BUILD_TESTS)
    enable_testing()
    add_executable(prisma_overlay_state_test
        tests/overlay_state_test.cpp
    )
    target_link_libraries(prisma_overlay_state_test PRIVATE prisma_overlay_state)
    add_test(NAME prisma_overlay_state_test COMMAND prisma_overlay_state_test)
endif()

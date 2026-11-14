#pragma once
#include "cocos2d.h"
#include <cstdint>
#if defined(_WIN32)
#define PRISMA_COCOS_EXPORT __declspec(dllexport)
#else
#define PRISMA_COCOS_EXPORT __attribute__((visibility("default")))
#endif
// Host contract version. The host must use the exact Cocos2d ABI of the game.
constexpr std::uint32_t PRISMA_COCOS_HOST_API_VERSION = 1;
// Called only from inside a compatible Cocos2d process, on its main thread.
// Scene pointers are borrowed and are not retained by this API.
extern "C" PRISMA_COCOS_EXPORT std::uint32_t prisma_cocos_host_api_version();
extern "C" PRISMA_COCOS_EXPORT cocos2d::Layer*
prisma_attach_overlay_to_scene(cocos2d::Scene* activeScene);
extern "C" PRISMA_COCOS_EXPORT bool
prisma_detach_overlay_from_scene(cocos2d::Scene* activeScene);

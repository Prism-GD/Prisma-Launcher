#pragma once

#include "cocos2d.h"

#if defined(_WIN32)
#define PRISMA_COCOS_EXPORT __declspec(dllexport)
#else
#define PRISMA_COCOS_EXPORT __attribute__((visibility("default")))
#endif

// Host contract: the caller must already be executing inside a compatible
// Cocos2d-x v3 process and call this on the Cocos2d main thread.
extern "C" PRISMA_COCOS_EXPORT cocos2d::Layer*
prisma_attach_overlay_to_scene(cocos2d::Scene* activeScene);

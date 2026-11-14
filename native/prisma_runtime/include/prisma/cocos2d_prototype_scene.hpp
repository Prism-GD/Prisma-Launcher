#pragma once

#include "cocos2d.h"

namespace prisma {

// Standalone host scene for validating the overlay before the game adapter
// exists. This scene is not Geometry Dash and does not load or modify its APK.
cocos2d::Scene* createCocos2dPrototypeScene();

} // namespace prisma

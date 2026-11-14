#include "prisma/cocos2d_host_api.hpp"
#include "prisma/cocos2d_overlay_layer.hpp"

extern "C" PRISMA_COCOS_EXPORT cocos2d::Layer*
prisma_attach_overlay_to_scene(cocos2d::Scene* activeScene) {
    return prisma::Cocos2dOverlayLayer::attachToScene(activeScene);
}

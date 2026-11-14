#include "prisma/cocos2d_host_api.hpp"
#include "prisma/cocos2d_overlay_layer.hpp"
namespace {
constexpr int kPrismaOverlayNodeTag = 0x50524953; // PRIS
}
extern "C" PRISMA_COCOS_EXPORT std::uint32_t prisma_cocos_host_api_version() {
    return PRISMA_COCOS_HOST_API_VERSION;
}
extern "C" PRISMA_COCOS_EXPORT cocos2d::Layer*
prisma_attach_overlay_to_scene(cocos2d::Scene* activeScene) {
    if (!activeScene) return nullptr;
    // Repeated host callbacks must not add duplicate overlay layers.
    if (auto* existing = activeScene->getChildByTag(kPrismaOverlayNodeTag))
        return static_cast<cocos2d::Layer*>(existing);
    auto* layer = prisma::Cocos2dOverlayLayer::create();
    if (!layer) return nullptr;
    layer->setTag(kPrismaOverlayNodeTag);
    activeScene->addChild(layer, 100000);
    return layer;
}
extern "C" PRISMA_COCOS_EXPORT bool
prisma_detach_overlay_from_scene(cocos2d::Scene* activeScene) {
    if (!activeScene) return false;
    auto* existing = activeScene->getChildByTag(kPrismaOverlayNodeTag);
    if (!existing) return false;
    existing->removeFromParentAndCleanup(true);
    return true;
}

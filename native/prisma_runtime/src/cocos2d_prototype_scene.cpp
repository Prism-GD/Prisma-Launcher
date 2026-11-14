#include "prisma/cocos2d_prototype_scene.hpp"
#include "prisma/cocos2d_overlay_layer.hpp"

USING_NS_CC;

namespace prisma {

Scene* createCocos2dPrototypeScene() {
    auto* scene = Scene::create();
    if (!scene) return nullptr;

    auto* background = LayerColor::create(Color4B(18, 14, 30, 255));
    if (background) scene->addChild(background, -1);

    auto* title = Label::createWithSystemFont(
        "PRISMA MICRO PROTOTYPE", "Arial-BoldMT", 20.0f);
    if (title) {
        title->setPosition(Director::getInstance()->getWinSize() * 0.5f);
        title->setTextColor(Color4B(220, 205, 255, 255));
        scene->addChild(title, 0);
    }

    auto* overlay = Cocos2dOverlayLayer::attachToScene(scene);
    if (overlay) {
        overlay->state().replaceMods({
            {"demo.practice", "Practice Helper (demo)", "0.1.0",
             "Example entry used only to test the in-game list", false, true},
            {"demo.incompatible", "Experimental Mod (demo)", "0.1.0",
             "Example of an incompatible mod", false, false},
        });
    }
    return scene;
}

} // namespace prisma

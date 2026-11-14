#pragma once

#include "prisma/overlay_state.hpp"

#include "cocos2d.h"

namespace prisma {

// Cocos2d-x v3 scene layer. The host integration must add this layer to the
// active Geometry Dash scene from a verified in-process entry point.
class Cocos2dOverlayLayer final : public cocos2d::Layer {
public:
    static Cocos2dOverlayLayer* create();

    bool init() override;
    void onEnter() override;
    void onExit() override;

    OverlayState& state() noexcept { return state_; }

private:
    void rebuildUi();
    bool onTouchBegan(cocos2d::Touch* touch, cocos2d::Event* event);

    OverlayState state_;
    cocos2d::Node* uiRoot_ = nullptr;
    cocos2d::EventListenerTouchOneByOne* touchListener_ = nullptr;
};

} // namespace prisma

#include "prisma/cocos2d_overlay_layer.hpp"

#include <algorithm>
#include <string>

USING_NS_CC;

namespace prisma {
namespace {
Color4F purple() { return Color4F(0.48f, 0.20f, 0.92f, 0.98f); }
Color4F panelColor() { return Color4F(0.055f, 0.045f, 0.10f, 0.97f); }
Color4F muted() { return Color4F(0.25f, 0.22f, 0.32f, 1.0f); }
}

Cocos2dOverlayLayer* Cocos2dOverlayLayer::create() {
    auto* layer = new (std::nothrow) Cocos2dOverlayLayer();
    if (layer && layer->init()) {
        layer->autorelease();
        return layer;
    }
    CC_SAFE_DELETE(layer);
    return nullptr;
}

Cocos2dOverlayLayer* Cocos2dOverlayLayer::attachToScene(Scene* scene) {
    if (!scene) return nullptr;
    auto* layer = Cocos2dOverlayLayer::create();
    if (layer) scene->addChild(layer, 100000);
    return layer;
}

bool Cocos2dOverlayLayer::init() {
    if (!Layer::init()) return false;
    const auto size = Director::getInstance()->getWinSize();
    setContentSize(size);
    setAnchorPoint(Vec2::ZERO);
    uiRoot_ = Node::create();
    addChild(uiRoot_, 100000);
    rebuildUi();
    return true;
}

void Cocos2dOverlayLayer::onEnter() {
    Layer::onEnter();
    touchListener_ = EventListenerTouchOneByOne::create();
    touchListener_->setSwallowTouches(true);
    touchListener_->onTouchBegan = CC_CALLBACK_2(Cocos2dOverlayLayer::onTouchBegan, this);
    _eventDispatcher->addEventListenerWithSceneGraphPriority(touchListener_, this);
}

void Cocos2dOverlayLayer::onExit() {
    if (touchListener_) {
        _eventDispatcher->removeEventListener(touchListener_);
        touchListener_ = nullptr;
    }
    Layer::onExit();
}

void Cocos2dOverlayLayer::rebuildUi() {
    if (!uiRoot_) return;
    uiRoot_->removeAllChildren();

    const Size size = Director::getInstance()->getWinSize();
    const float orbRadius = std::max(22.0f, std::min(size.width, size.height) * 0.035f);
    const Vec2 orbCenter(size.width - orbRadius - 18.0f, orbRadius + 42.0f);

    if (state_.isPanelOpen()) {
        const float panelW = std::min(size.width - 32.0f, 440.0f);
        const float panelH = std::min(size.height - 100.0f, 520.0f);
        const Vec2 origin((size.width - panelW) * 0.5f, (size.height - panelH) * 0.5f);

        auto* panel = DrawNode::create();
        panel->drawSolidRect(origin, origin + Vec2(panelW, panelH), panelColor());
        uiRoot_->addChild(panel);

        auto* title = Label::createWithSystemFont("PRISMA", "Arial", 22.0f);
        title->setAnchorPoint(Vec2(0.0f, 0.5f));
        title->setPosition(origin + Vec2(18.0f, panelH - 28.0f));
        title->setTextColor(Color4B::WHITE);
        uiRoot_->addChild(title);

        const char* names[] = {"Mods", "Browse", "Settings", "Logs"};
        const float tabWidth = (panelW - 24.0f) / 4.0f;
        for (int i = 0; i < 4; ++i) {
            const bool selected = static_cast<int>(state_.selectedTab()) == i;
            auto* tab = Label::createWithSystemFont(names[i], "Arial", 13.0f);
            tab->setPosition(origin + Vec2(12.0f + tabWidth * (i + 0.5f), panelH - 66.0f));
            tab->setTextColor(selected ? Color4B(190, 145, 255, 255) : Color4B(170, 165, 180, 255));
            uiRoot_->addChild(tab);

            auto* underline = DrawNode::create();
            if (selected) {
                underline->drawSolidRect(
                    origin + Vec2(12.0f + tabWidth * i, panelH - 82.0f),
                    origin + Vec2(12.0f + tabWidth * (i + 1) - 5.0f, panelH - 79.0f),
                    purple());
            }
            uiRoot_->addChild(underline);
        }

        std::string subtitle;
        switch (state_.selectedTab()) {
            case OverlayTab::Mods: subtitle = "Installed mods"; break;
            case OverlayTab::Browse: subtitle = "Discover community mods"; break;
            case OverlayTab::Settings: subtitle = "Runtime settings"; break;
            case OverlayTab::Logs: subtitle = "Runtime logs"; break;
        }
        auto* section = Label::createWithSystemFont(subtitle, "Arial", 16.0f);
        section->setAnchorPoint(Vec2(0.0f, 0.5f));
        section->setPosition(origin + Vec2(18.0f, panelH - 112.0f));
        section->setTextColor(Color4B::WHITE);
        uiRoot_->addChild(section);

        if (state_.selectedTab() == OverlayTab::Mods) {
            if (state_.mods().empty()) {
                auto* empty = Label::createWithSystemFont(
                    "No mods installed yet", "Arial", 14.0f);
                empty->setPosition(origin + Vec2(panelW * 0.5f, panelH * 0.5f - 20.0f));
                empty->setTextColor(Color4B(175, 170, 185, 255));
                uiRoot_->addChild(empty);
            } else {
                float y = panelH - 155.0f;
                for (const auto& mod : state_.mods()) {
                    if (y < 28.0f) break;
                    auto* card = DrawNode::create();
                    card->drawSolidRect(origin + Vec2(12.0f, y - 42.0f),
                                        origin + Vec2(panelW - 12.0f, y + 4.0f),
                                        Color4F(0.13f, 0.11f, 0.19f, 1.0f));
                    uiRoot_->addChild(card);
                    auto* label = Label::createWithSystemFont(
                        mod.name + "  " + mod.version, "Arial", 13.0f);
                    label->setAnchorPoint(Vec2(0.0f, 0.5f));
                    label->setPosition(origin + Vec2(22.0f, y - 18.0f));
                    label->setTextColor(Color4B::WHITE);
                    uiRoot_->addChild(label);
                    y -= 54.0f;
                }
            }
        } else if (state_.selectedTab() == OverlayTab::Browse) {
            auto* hint = Label::createWithSystemFont(
                "Catalog connection is the next milestone", "Arial", 13.0f);
            hint->setPosition(origin + Vec2(panelW * 0.5f, panelH * 0.5f - 20.0f));
            hint->setTextColor(Color4B(175, 170, 185, 255));
            uiRoot_->addChild(hint);
        }
    }

    if (state_.isOrbVisible()) {
        auto* orb = DrawNode::create();
        orb->drawSolidCircle(orbCenter, orbRadius, 0.0f, 48, purple());
        orb->drawSolidCircle(orbCenter, orbRadius * 0.72f, 0.0f, 48,
                             Color4F(0.30f, 0.10f, 0.65f, 1.0f));
        uiRoot_->addChild(orb, 10);
        auto* mark = Label::createWithSystemFont("P", "Arial-BoldMT", orbRadius);
        mark->setPosition(orbCenter);
        mark->setTextColor(Color4B::WHITE);
        uiRoot_->addChild(mark, 11);
    }
}

bool Cocos2dOverlayLayer::onTouchBegan(Touch* touch, Event*) {
    if (!touch) return false;
    const Vec2 point = convertToNodeSpace(touch->getLocation());
    const Size size = Director::getInstance()->getWinSize();
    const float radius = std::max(22.0f, std::min(size.width, size.height) * 0.035f);
    const Vec2 orbCenter(size.width - radius - 18.0f, radius + 42.0f);

    if (point.distance(orbCenter) <= radius + 10.0f) {
        state_.toggleOrb();
        rebuildUi();
        return true;
    }

    if (!state_.isPanelOpen()) return false;

    const float panelW = std::min(size.width - 32.0f, 440.0f);
    const float panelH = std::min(size.height - 100.0f, 520.0f);
    const Vec2 origin((size.width - panelW) * 0.5f, (size.height - panelH) * 0.5f);

    if (point.x < origin.x || point.x > origin.x + panelW ||
        point.y < origin.y || point.y > origin.y + panelH) {
        state_.closePanel();
        rebuildUi();
        return true;
    }

    const float tabY = origin.y + panelH - 48.0f;
    if (point.y >= tabY - 24.0f && point.y <= tabY + 12.0f) {
        const float tabWidth = panelW / 4.0f;
        const int tab = std::clamp(static_cast<int>((point.x - origin.x) / tabWidth), 0, 3);
        state_.selectTab(static_cast<OverlayTab>(tab));
        rebuildUi();
    }
    return true;
}

} // namespace prisma

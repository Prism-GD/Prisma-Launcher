#pragma once

#include "prisma/overlay_state.hpp"

namespace prisma {

// Receives touch coordinates in framebuffer pixels from the game's input
// callback. It does not install hooks or intercept input by itself.
class GameOverlayInput final {
public:
    explicit GameOverlayInput(OverlayState& state) : state_(state) {}
    bool onTouchUp(float x, float y, int width, int height) noexcept;

private:
    OverlayState& state_;
};

} // namespace prisma

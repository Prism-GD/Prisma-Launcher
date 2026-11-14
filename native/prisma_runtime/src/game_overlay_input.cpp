#include "prisma/game_overlay_input.hpp"

#include <algorithm>

namespace prisma {

bool GameOverlayInput::onTouchUp(float x, float y, int width, int height) noexcept {
    if (width <= 0 || height <= 0) return false;

    const float w = static_cast<float>(width);
    const float h = static_cast<float>(height);
    const float radius = std::max(22.0f, std::min(w, h) * 0.035f);
    const float orbX = w - radius - 18.0f;
    const float orbY = h - radius - 42.0f;

    if (x >= orbX - radius && x <= orbX + radius &&
        y >= orbY - radius && y <= orbY + radius) {
        state_.toggleOrb();
        return true;
    }

    if (!state_.isPanelOpen()) return false;

    const float panelW = std::min(w - 32.0f, 440.0f);
    const float panelH = std::min(h - 100.0f, 520.0f);
    const float left = (w - panelW) * 0.5f;
    const float top = (h - panelH) * 0.5f;

    if (x < left || x > left + panelW || y < top || y > top + panelH) {
        state_.closePanel();
        return true;
    }

    if (y >= top + 18.0f && y <= top + 48.0f) {
        const float tabWidth = panelW / 4.0f;
        const int tab = std::clamp(static_cast<int>((x - left) / tabWidth), 0, 3);
        state_.selectTab(static_cast<OverlayTab>(tab));
        return true;
    }

    return true; // Touch is inside Prisma's panel; reserve it from game controls.
}

} // namespace prisma

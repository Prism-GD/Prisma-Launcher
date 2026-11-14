#pragma once

#include "prisma/overlay_state.hpp"

namespace prisma {

// Must be called on the Geometry Dash OpenGL ES render thread, after the
// game's GL context is current. Coordinates are supplied in framebuffer pixels.
class GameOverlayRenderer final {
public:
    GameOverlayRenderer() = default;
    ~GameOverlayRenderer();

    GameOverlayRenderer(const GameOverlayRenderer&) = delete;
    GameOverlayRenderer& operator=(const GameOverlayRenderer&) = delete;

    bool initialize();
    void shutdown() noexcept;
    void draw(const OverlayState& state, int framebufferWidth, int framebufferHeight);

private:
    unsigned int program_ = 0;
    int positionAttribute_ = -1;
    int colorUniform_ = -1;
    int screenUniform_ = -1;
};

} // namespace prisma

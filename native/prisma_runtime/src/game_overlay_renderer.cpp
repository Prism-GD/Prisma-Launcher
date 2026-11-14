#include "prisma/game_overlay_renderer.hpp"

#ifdef __ANDROID__
#include <GLES2/gl2.h>

#include <algorithm>
#include <array>
#include <cmath>

namespace prisma {
namespace {
constexpr const char* kVertexShader =
    "attribute vec2 aPosition; uniform vec2 uScreen; "
    "void main(){ vec2 p=(aPosition/uScreen)*2.0-1.0; "
    "gl_Position=vec4(p.x,-p.y,0.0,1.0); }";
constexpr const char* kFragmentShader =
    "precision mediump float; uniform vec4 uColor; "
    "void main(){ gl_FragColor=uColor; }";

GLuint compileShader(GLenum type, const char* source) {
    GLuint shader = glCreateShader(type);
    glShaderSource(shader, 1, &source, nullptr);
    glCompileShader(shader);
    GLint ok = GL_FALSE;
    glGetShaderiv(shader, GL_COMPILE_STATUS, &ok);
    if (ok != GL_TRUE) {
        glDeleteShader(shader);
        return 0;
    }
    return shader;
}
} // namespace

GameOverlayRenderer::~GameOverlayRenderer() { shutdown(); }

bool GameOverlayRenderer::initialize() {
    if (program_ != 0) return true;
    const GLuint vertex = compileShader(GL_VERTEX_SHADER, kVertexShader);
    const GLuint fragment = compileShader(GL_FRAGMENT_SHADER, kFragmentShader);
    if (!vertex || !fragment) {
        if (vertex) glDeleteShader(vertex);
        if (fragment) glDeleteShader(fragment);
        return false;
    }
    program_ = glCreateProgram();
    glAttachShader(program_, vertex);
    glAttachShader(program_, fragment);
    glBindAttribLocation(program_, 0, "aPosition");
    glLinkProgram(program_);
    glDeleteShader(vertex);
    glDeleteShader(fragment);

    GLint linked = GL_FALSE;
    glGetProgramiv(program_, GL_LINK_STATUS, &linked);
    if (linked != GL_TRUE) {
        shutdown();
        return false;
    }
    positionAttribute_ = 0;
    colorUniform_ = glGetUniformLocation(program_, "uColor");
    screenUniform_ = glGetUniformLocation(program_, "uScreen");
    return colorUniform_ >= 0 && screenUniform_ >= 0;
}

void GameOverlayRenderer::shutdown() noexcept {
    if (program_ != 0) glDeleteProgram(program_);
    program_ = 0;
    positionAttribute_ = colorUniform_ = screenUniform_ = -1;
}

void GameOverlayRenderer::draw(
    const OverlayState& state, int framebufferWidth, int framebufferHeight) {
    if (!state.isOrbVisible() || framebufferWidth <= 0 || framebufferHeight <= 0 ||
        !initialize()) return;

    // Preserve the most important GL state so the game's next draw is not
    // accidentally rendered with Prisma's shader or blend configuration.
    GLint oldProgram = 0;
    GLint oldArrayBuffer = 0;
    GLint oldViewport[4] = {};
    GLboolean blendWasEnabled = glIsEnabled(GL_BLEND);
    glGetIntegerv(GL_CURRENT_PROGRAM, &oldProgram);
    glGetIntegerv(GL_ARRAY_BUFFER_BINDING, &oldArrayBuffer);
    glGetIntegerv(GL_VIEWPORT, oldViewport);

    glUseProgram(program_);
    glUniform2f(screenUniform_, static_cast<float>(framebufferWidth),
                static_cast<float>(framebufferHeight));
    glEnable(GL_BLEND);
    glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
    glViewport(0, 0, framebufferWidth, framebufferHeight);
    glBindBuffer(GL_ARRAY_BUFFER, 0);
    glEnableVertexAttribArray(static_cast<GLuint>(positionAttribute_));

    auto rect = [&](float left, float top, float right, float bottom,
                    float r, float g, float b, float a) {
        const std::array<GLfloat, 12> vertices = {
            left, top, right, top, left, bottom,
            left, bottom, right, top, right, bottom
        };
        glUniform4f(colorUniform_, r, g, b, a);
        glVertexAttribPointer(static_cast<GLuint>(positionAttribute_), 2,
                              GL_FLOAT, GL_FALSE, 0, vertices.data());
        glDrawArrays(GL_TRIANGLES, 0, 6);
    };

    const float w = static_cast<float>(framebufferWidth);
    const float h = static_cast<float>(framebufferHeight);
    const float orbRadius = std::max(22.0f, std::min(w, h) * 0.035f);
    const float orbX = w - orbRadius - 18.0f;
    const float orbY = h - orbRadius - 42.0f;

    if (state.isPanelOpen()) {
        const float panelW = std::min(w - 32.0f, 440.0f);
        const float panelH = std::min(h - 100.0f, 520.0f);
        const float left = (w - panelW) * 0.5f;
        const float top = (h - panelH) * 0.5f;
        rect(left, top, left + panelW, top + panelH, 0.055f, 0.045f, 0.10f, 0.96f);
        rect(left + 12.0f, top + 58.0f, left + panelW - 12.0f,
             top + 60.0f, 0.55f, 0.30f, 0.95f, 1.0f);
        // Four visible tab markers; text and detailed controls are supplied
        // by the next UI layer, not faked here.
        const float tabWidth = (panelW - 40.0f) / 4.0f;
        for (int i = 0; i < 4; ++i) {
            const bool selected = static_cast<int>(state.selectedTab()) == i;
            rect(left + 12.0f + tabWidth * i, top + 28.0f,
                 left + 12.0f + tabWidth * (i + 1) - 4.0f, top + 34.0f,
                 selected ? 0.72f : 0.28f, selected ? 0.42f : 0.25f,
                 selected ? 1.0f : 0.38f, 1.0f);
        }
    }

    // Purple orb, drawn as a compact square-backed control for this first pass.
    rect(orbX - orbRadius, orbY - orbRadius, orbX + orbRadius, orbY + orbRadius,
         0.48f, 0.20f, 0.92f, 0.96f);

    glDisableVertexAttribArray(static_cast<GLuint>(positionAttribute_));
    glBindBuffer(GL_ARRAY_BUFFER, static_cast<GLuint>(oldArrayBuffer));
    glUseProgram(static_cast<GLuint>(oldProgram));
    glViewport(oldViewport[0], oldViewport[1], oldViewport[2], oldViewport[3]);
    if (!blendWasEnabled) glDisable(GL_BLEND);
}
} // namespace prisma

#else
namespace prisma {
GameOverlayRenderer::~GameOverlayRenderer() = default;
bool GameOverlayRenderer::initialize() { return false; }
void GameOverlayRenderer::shutdown() noexcept {}
void GameOverlayRenderer::draw(const OverlayState&, int, int) {}
} // namespace prisma
#endif

#include "prisma/overlay_state.hpp"

#include <algorithm>
#include <unordered_set>
#include <utility>

namespace prisma {

void OverlayState::toggleOrb() noexcept {
    if (panelOpen_) {
        closePanel();
    } else {
        openPanel();
    }
}

void OverlayState::openPanel() noexcept {
    orbVisible_ = true;
    panelOpen_ = true;
}

void OverlayState::closePanel() noexcept {
    panelOpen_ = false;
}

void OverlayState::selectTab(OverlayTab tab) noexcept {
    selectedTab_ = tab;
}

bool OverlayState::isOrbVisible() const noexcept {
    return orbVisible_;
}

bool OverlayState::isPanelOpen() const noexcept {
    return panelOpen_;
}

OverlayTab OverlayState::selectedTab() const noexcept {
    return selectedTab_;
}

bool OverlayState::replaceMods(std::vector<ModItem> mods) {
    std::unordered_set<std::string> ids;
    for (const auto& mod : mods) {
        if (mod.id.empty() || !ids.insert(mod.id).second) {
            return false;
        }
    }
    mods_ = std::move(mods);
    return true;
}

const std::vector<ModItem>& OverlayState::mods() const noexcept {
    return mods_;
}

bool OverlayState::setModEnabled(const std::string& id, bool enabled) noexcept {
    const auto found = std::find_if(
        mods_.begin(),
        mods_.end(),
        [&id](const ModItem& mod) { return mod.id == id; }
    );

    if (found == mods_.end() || (enabled && !found->compatible)) {
        return false;
    }

    found->enabled = enabled;
    return true;
}

} // namespace prisma

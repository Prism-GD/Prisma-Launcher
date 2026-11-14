#pragma once

#include <cstddef>
#include <string>
#include <vector>

namespace prisma {

enum class OverlayTab {
    Mods,
    Browse,
    Settings,
    Logs,
};

struct ModItem {
    std::string id;
    std::string name;
    std::string version;
    std::string description;
    bool enabled = false;
    bool compatible = false;
};

class OverlayState final {
public:
    void toggleOrb() noexcept;
    void openPanel() noexcept;
    void closePanel() noexcept;
    void selectTab(OverlayTab tab) noexcept;

    bool isOrbVisible() const noexcept;
    bool isPanelOpen() const noexcept;
    OverlayTab selectedTab() const noexcept;

    bool replaceMods(std::vector<ModItem> mods);
    const std::vector<ModItem>& mods() const noexcept;
    bool setModEnabled(const std::string& id, bool enabled) noexcept;

private:
    bool orbVisible_ = true;
    bool panelOpen_ = false;
    OverlayTab selectedTab_ = OverlayTab::Mods;
    std::vector<ModItem> mods_;
};

} // namespace prisma

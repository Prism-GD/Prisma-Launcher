#include "prisma/overlay_state.hpp"

#include <cassert>
#include <vector>

int main() {
    prisma::OverlayState state;

    assert(state.isOrbVisible());
    assert(!state.isPanelOpen());
    assert(state.selectedTab() == prisma::OverlayTab::Mods);

    state.toggleOrb();
    assert(state.isPanelOpen());

    state.selectTab(prisma::OverlayTab::Settings);
    assert(state.selectedTab() == prisma::OverlayTab::Settings);

    state.toggleOrb();
    assert(!state.isPanelOpen());

    const bool accepted = state.replaceMods({
        {"demo.enabled", "Demo Enabled", "1.0.0", "Compatible test mod", false, true},
        {"demo.incompatible", "Demo Incompatible", "1.0.0", "Not compatible", false, false},
    });
    assert(accepted);
    assert(state.mods().size() == 2);

    assert(state.setModEnabled("demo.enabled", true));
    assert(state.mods()[0].enabled);
    assert(!state.setModEnabled("demo.incompatible", true));
    assert(!state.setModEnabled("missing.mod", true));

    const bool duplicateRejected = state.replaceMods({
        {"duplicate", "One", "1", "", false, true},
        {"duplicate", "Two", "1", "", false, true},
    });
    assert(!duplicateRejected);
    assert(state.mods().size() == 2);

    return 0;
}

package com.epherical.professions.listener.perks;

import com.epherical.professions.PerkManager;
import com.epherical.professions.api.event.EventListener;
import com.epherical.professions.api.event.runtime.perks.PerkClaimedEvent;
import net.minecraft.server.level.ServerPlayer;

public class PerkClaimListener implements EventListener<PerkClaimedEvent> {

    private final PerkManager perkManager;

    public PerkClaimListener(PerkManager perkManager) {
        this.perkManager = perkManager;
    }

    @Override
    public void handle(PerkClaimedEvent event) {

        if (!event.getClaimedPerkIds().isEmpty()) {
            perkManager.playerJoined(event.getPlayer(), (ServerPlayer) event.getPlayer().getPlayer());
        }


    }
}

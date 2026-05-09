package com.epherical.professions.listener.perks;

import com.epherical.professions.PerkManager;
import com.epherical.professions.api.event.EventListener;
import com.epherical.professions.api.event.runtime.perks.PerkClaimedEvent;
import com.epherical.professions.model.perks.Perk;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

public class PerkClaimListener implements EventListener<PerkClaimedEvent> {

    private final PerkManager perkManager;

    public PerkClaimListener(PerkManager perkManager) {
        this.perkManager = perkManager;
    }

    @Override
    public void handle(PerkClaimedEvent event) {

        for (ResourceLocation claimedPerkId : event.getClaimedPerkIds()) {
            Perk perk = perkManager.getPerk(claimedPerkId);
            // todo; we have to recalculate all startup perks
            if (perk != null) {
                perk.onActivate(event.getOccupation(), event.getPlayer(), (ServerPlayer) event.getPlayer().getPlayer());
            }

        }

    }
}

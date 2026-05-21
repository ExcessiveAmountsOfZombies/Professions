package com.epherical.professions.networking.server;

import com.epherical.professions.PerkManager;
import com.epherical.professions.PlayerManager;
import com.epherical.professions.ProfessionsCommon;
import com.epherical.professions.api.IProfessionalPlayer;
import com.epherical.professions.api.event.runtime.perks.PerkClaimedEvent;
import com.epherical.professions.model.Occupation;
import com.epherical.professions.networking.client.C2SOccupationPerkClaimPayload;
import com.google.common.collect.ImmutableSet;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

import java.util.Set;

public final class OccupationPerkClaimPayloadHandler {

    public static void handle(ServerPlayer serverPlayer, C2SOccupationPerkClaimPayload payload) {
        PlayerManager playerManager = ProfessionsCommon.INSTANCE.getPlayerManager();
        PerkManager perkManager = ProfessionsCommon.INSTANCE.getPerkManager();
        IProfessionalPlayer professionalPlayer = playerManager.getPlayer(serverPlayer.getUUID());
        if (professionalPlayer == null) {
            ProfessionsCommon.LOG.error("Player {} tried to claim some perks for {}, but they don't exist on the server. UHHHH",
                    serverPlayer.getScoreboardName(), payload.professionId());
            return;
        }

        if (!perkManager.arePerksEnabled(professionalPlayer)) {
            return; // an extra precaution
        }

        Occupation occupation = professionalPlayer.getOccupation(payload.professionId());
        if (occupation == null) {
            return;
        }

        ImmutableSet.Builder<ResourceLocation> claimedPerkIds = new ImmutableSet.Builder<>();
        for (ResourceLocation perkId : payload.claimedPerks()) {
            if (!occupation.hasUnclaimedPerk(perkId)) {
                ProfessionsCommon.LOG.warn("Player {} attempted to claim invalid perk {} for occupation {}",
                        serverPlayer.getScoreboardName(), perkId, payload.professionId());
                continue;
            }

            occupation.addClaimedPerk(perkId);
            claimedPerkIds.add(perkId);
        }
        Set<ResourceLocation> build = claimedPerkIds.build();

        if (!build.isEmpty()) {
            professionalPlayer.markDirty(true);
            ProfessionsCommon.INSTANCE.getEventBus().post(new PerkClaimedEvent(occupation, professionalPlayer, build));
        }

        PlayerDataSyncUtil.syncAll(serverPlayer, professionalPlayer, playerManager);
    }
}

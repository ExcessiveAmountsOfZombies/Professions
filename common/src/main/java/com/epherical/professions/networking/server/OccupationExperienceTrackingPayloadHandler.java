package com.epherical.professions.networking.server;

import com.epherical.professions.PlayerManager;
import com.epherical.professions.ProfessionsCommon;
import com.epherical.professions.api.IProfessionalPlayer;
import com.epherical.professions.model.Occupation;
import com.epherical.professions.networking.client.C2SOccupationExperienceTrackingPayload;
import net.minecraft.server.level.ServerPlayer;

public final class OccupationExperienceTrackingPayloadHandler {

    private OccupationExperienceTrackingPayloadHandler() {
    }

    public static void handle(ServerPlayer serverPlayer, C2SOccupationExperienceTrackingPayload payload) {
        PlayerManager playerManager = ProfessionsCommon.INSTANCE.getPlayerManager();
        IProfessionalPlayer professionalPlayer = playerManager.getPlayer(serverPlayer.getUUID());
        if (professionalPlayer == null) {
            playerManager.playerJoined(serverPlayer);
            professionalPlayer = playerManager.getPlayer(serverPlayer.getUUID());
            if (professionalPlayer == null) {
                return;
            }
        }

        Occupation occupation = professionalPlayer.getOccupation(payload.professionId());
        if (occupation != null) {
            occupation.setExperienceGainTrackingEnabled(payload.trackingEnabled());
        }
    }
}

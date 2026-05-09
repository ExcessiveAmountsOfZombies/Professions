package com.epherical.professions.networking.server;

import com.epherical.professions.PlayerManager;
import com.epherical.professions.ProfessionsCommon;
import com.epherical.professions.api.IProfessionalPlayer;
import com.epherical.professions.model.Occupation;
import com.epherical.professions.networking.client.C2SOccupationExperienceTrackingPayload;
import net.minecraft.server.level.ServerPlayer;

public final class OccupationExperienceTrackingPayloadHandler {

    public static void handle(ServerPlayer serverPlayer, C2SOccupationExperienceTrackingPayload payload) {
        PlayerManager playerManager = ProfessionsCommon.INSTANCE.getPlayerManager();
        IProfessionalPlayer professionalPlayer = playerManager.getPlayer(serverPlayer.getUUID());
        if (professionalPlayer == null) {
            ProfessionsCommon.LOG.error("Player {} tried to change exp tracking for {}, but they don't exist on the server. UHHHH",
                    serverPlayer.getScoreboardName(), payload.professionId());
            return;
        }

        Occupation occupation = professionalPlayer.getOccupation(payload.professionId());
        if (occupation != null) {
            occupation.setExperienceGainTrackingEnabled(payload.trackingEnabled());
        }
    }
}

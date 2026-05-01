package com.epherical.professions.listener;

import com.epherical.professions.api.IProfessionalPlayer;
import com.epherical.professions.api.event.EventListener;
import com.epherical.professions.api.event.runtime.rewards.OccupationExperienceEvent;
import com.epherical.professions.core.context.ProfessionParameter;
import com.epherical.professions.networking.NetworkPayloadDispatcher;
import com.epherical.professions.networking.S2CExperienceGainPayload;
import net.minecraft.server.level.ServerPlayer;

public class GainExpNotificationListener implements EventListener<OccupationExperienceEvent> {


    @Override
    public void handle(OccupationExperienceEvent event) {
        IProfessionalPlayer player = event.getContext().getPossibleParameter(ProfessionParameter.THIS_PLAYER);
        if (player == null) {
            return;
        }

        ServerPlayer serverPlayer = player.getPlayer();
        if (serverPlayer == null) {
            return;
        }

        S2CExperienceGainPayload packet = new S2CExperienceGainPayload(
                event.getOccupation().getProfession().value().displayNameRaw(),
                event.getNewAmount()
        );

        NetworkPayloadDispatcher.sendToPlayer(serverPlayer, packet);
    }
}

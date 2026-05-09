package com.epherical.professions.listener.notification;

import com.epherical.professions.api.IProfessionalPlayer;
import com.epherical.professions.api.event.EventListener;
import com.epherical.professions.api.event.runtime.rewards.OccupationExperienceEvent;
import com.epherical.professions.core.context.ProfessionParameter;
import com.epherical.professions.model.Occupation;
import com.epherical.professions.networking.NetworkPayloadDispatcher;
import com.epherical.professions.networking.server.S2CExperienceGainPayload;
import net.minecraft.server.level.ServerPlayer;

public class NotificationGainExperienceListener implements EventListener<OccupationExperienceEvent> {


    @Override
    public void handle(OccupationExperienceEvent event) {
        IProfessionalPlayer player = event.getContext().getPossibleParameter(ProfessionParameter.THIS_PLAYER);
        if (player == null) {
            return;
        }

        if (player.getPlayer() instanceof ServerPlayer serverPlayer) {
            Occupation occupation = event.getOccupation();
            S2CExperienceGainPayload packet = new S2CExperienceGainPayload(
                    event.getOccupation().getProfessionKey(),
                    event.getNewAmount()
            );
            NetworkPayloadDispatcher.sendToPlayer(serverPlayer, packet);
        }
    }
}

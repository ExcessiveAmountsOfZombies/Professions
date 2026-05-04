package com.epherical.professions.networking.client;

import com.epherical.professions.ProfessionsCommon;
import com.epherical.professions.networking.server.S2CExperienceGainPayload;
import net.minecraft.client.Minecraft;
import net.minecraft.core.RegistryAccess;

public final class ExperienceOccupationSyncHandler {

    private ExperienceOccupationSyncHandler() {
    }

    public static void handle(S2CExperienceGainPayload payload) {
        Minecraft.getInstance().doRunTask(() -> {
            Minecraft minecraft = Minecraft.getInstance();
            if (minecraft.player == null) {
                return;
            }

            RegistryAccess registryAccess = minecraft.level != null ? minecraft.level.registryAccess() : null;
            ProfessionsCommon.INSTANCE.getPlayerManager()
                    .applyClientExperienceGain(minecraft.player.getUUID(), payload.professionId(), payload.experienceGained(), registryAccess);
        });
    }
}

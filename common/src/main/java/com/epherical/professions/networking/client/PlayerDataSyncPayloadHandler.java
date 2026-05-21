package com.epherical.professions.networking.client;

import com.epherical.professions.ProfessionsCommon;
import com.epherical.professions.networking.server.S2CPlayerDataSyncPayload;
import net.minecraft.client.Minecraft;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;

public final class PlayerDataSyncPayloadHandler {

    private PlayerDataSyncPayloadHandler() {
    }

    public static void handle(S2CPlayerDataSyncPayload payload) {
        Minecraft.getInstance().doRunTask(() -> {
            Minecraft minecraft = Minecraft.getInstance();
            if (!minecraft.isSingleplayer()) {
                RegistryAccess registryAccess = minecraft.level != null ? minecraft.level.registryAccess() : null;
                ResourceLocation categoryId = payload.categoryId().orElse(null);
                ProfessionsCommon.INSTANCE.getPlayerManager().applyClientOccupationSync(
                        payload.playerId(), payload.occupations(), categoryId, registryAccess);
            }
        });
    }
}

package com.epherical.professions.networking.client;

import com.epherical.professions.ProfessionsCommon;
import com.epherical.professions.networking.server.S2CPlayerActionsSyncPayload;
import net.minecraft.client.Minecraft;
import net.minecraft.core.RegistryAccess;

public final class PlayerActionsSyncPayloadHandler {

    private PlayerActionsSyncPayloadHandler() {
    }

    public static void handle(S2CPlayerActionsSyncPayload payload) {
        Minecraft.getInstance().execute(() -> {
            Minecraft minecraft = Minecraft.getInstance();
            if (!minecraft.isSingleplayer()) {
                RegistryAccess registryAccess = minecraft.level != null ? minecraft.level.registryAccess() : null;
                ProfessionsCommon.INSTANCE.getPlayerManager().applyClientActionSync(payload.actions(), registryAccess);
            }
        });
    }
}

package com.epherical.professions.networking.client;

import com.epherical.professions.ProfessionsCommon;
import com.epherical.professions.networking.server.S2CPlayerGatesSyncPayload;
import net.minecraft.client.Minecraft;
import net.minecraft.core.RegistryAccess;

public final class PlayerGatesSyncPayloadHandler {

    private PlayerGatesSyncPayloadHandler() {
    }

    public static void handle(S2CPlayerGatesSyncPayload payload) {
        Minecraft.getInstance().doRunTask(() -> {
            Minecraft minecraft = Minecraft.getInstance();
            if (!minecraft.isSingleplayer()) {
                RegistryAccess registryAccess = minecraft.level != null ? minecraft.level.registryAccess() : null;
                ProfessionsCommon.INSTANCE.getPlayerManager().applyClientGateSync(payload.gates(), registryAccess);
            }
        });
    }
}

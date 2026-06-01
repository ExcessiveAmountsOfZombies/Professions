package com.epherical.professions.networking.client;

import com.epherical.professions.ProfessionsCommon;
import com.epherical.professions.networking.server.S2CPlayerPerksSyncPayload;
import net.minecraft.client.Minecraft;

public final class PlayerPerksSyncPayloadHandler {

    private PlayerPerksSyncPayloadHandler() {
    }

    public static void handle(S2CPlayerPerksSyncPayload payload) {
        Minecraft.getInstance().execute(() -> {
            Minecraft minecraft = Minecraft.getInstance();
            if (!minecraft.isSingleplayer()) {
                ProfessionsCommon.INSTANCE.getPlayerManager().applyClientPerkSync(payload.perks());
            }
        });
    }
}

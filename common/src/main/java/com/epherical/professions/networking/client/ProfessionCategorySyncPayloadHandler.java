package com.epherical.professions.networking.client;

import com.epherical.professions.ProfessionsCommon;
import com.epherical.professions.networking.server.S2CCategorySyncPayload;
import net.minecraft.client.Minecraft;

public final class ProfessionCategorySyncPayloadHandler {

    private ProfessionCategorySyncPayloadHandler() {
    }

    public static void handle(S2CCategorySyncPayload payload) {
        Minecraft.getInstance().execute(() -> ProfessionsCommon.INSTANCE.getCategoryManager().reloadCategories(payload.categories()));
    }
}

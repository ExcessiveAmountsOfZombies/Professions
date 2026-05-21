package com.epherical.professions.networking.server;

import com.epherical.professions.PlayerManager;
import com.epherical.professions.ProfessionsCommon;
import com.epherical.professions.api.IProfessionalPlayer;
import com.epherical.professions.core.ProfessionCategory;
import com.epherical.professions.networking.client.C2SCategorySelectionPayload;
import net.minecraft.server.level.ServerPlayer;

public final class CategorySelectionPayloadHandler {

    public static void handle(ServerPlayer serverPlayer, C2SCategorySelectionPayload payload) {
        PlayerManager playerManager = ProfessionsCommon.INSTANCE.getPlayerManager();
        IProfessionalPlayer professionalPlayer = playerManager.getPlayer(serverPlayer.getUUID());
        if (professionalPlayer == null) {
            ProfessionsCommon.LOG.error("Player {} tried to select a category, but they don't exist on the server. UHHHH",
                    serverPlayer.getScoreboardName());
            return;
        }

        if (professionalPlayer.getCategory() == null) {
            ProfessionCategory category = ProfessionsCommon.INSTANCE.getCategoryManager().getCategory(payload.categoryId());
            if (category != null) {
                professionalPlayer.setCategory(category);
            }
        }
        // todo; i think we need a way to update the perks if the player switches categories.

        PlayerDataSyncUtil.syncAll(serverPlayer, professionalPlayer, playerManager);
    }
}

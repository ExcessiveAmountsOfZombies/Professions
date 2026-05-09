package com.epherical.professions.networking.server;

import com.epherical.professions.PlayerManager;
import com.epherical.professions.ProfessionsCommon;
import com.epherical.professions.api.IProfessionalPlayer;
import com.epherical.professions.core.ProfessionCategory;
import com.epherical.professions.networking.NetworkPayloadDispatcher;
import com.epherical.professions.networking.client.C2SCategorySelectionPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

import java.util.Optional;

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

        ResourceLocation categoryId = playerManager.getCategoryIdFor(professionalPlayer);
        S2CPlayerDataSyncPayload syncPayload = new S2CPlayerDataSyncPayload(
                serverPlayer.getUUID(),
                professionalPlayer.getAllOccupations(),
                Optional.ofNullable(categoryId),
                playerManager.getRelevantActionsForCategory(professionalPlayer.getCategory()),
                playerManager.getAllPerks(professionalPlayer.getCategory())
        );
        NetworkPayloadDispatcher.sendToPlayer(serverPlayer, syncPayload);
    }
}

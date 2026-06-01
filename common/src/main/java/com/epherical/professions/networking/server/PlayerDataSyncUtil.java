package com.epherical.professions.networking.server;

import com.epherical.professions.PlayerManager;
import com.epherical.professions.api.IProfessionalPlayer;
import com.epherical.professions.networking.NetworkPayloadDispatcher;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;

import java.util.Optional;

public final class PlayerDataSyncUtil {
    // todo; when we run this command, we should check if the items are enabled.
    //  this applies for Perks and Gates.

    public static void syncAll(ServerPlayer player, IProfessionalPlayer professionalPlayer, PlayerManager playerManager) {
        syncOccupations(player, professionalPlayer, playerManager);
        syncActions(player, professionalPlayer, playerManager);
        syncPerks(player, professionalPlayer, playerManager);
        syncGates(player, professionalPlayer, playerManager);
    }

    public static void syncOccupations(ServerPlayer player, IProfessionalPlayer professionalPlayer, PlayerManager playerManager) {
        Identifier categoryId = playerManager.getCategoryIdFor(professionalPlayer);
        S2CPlayerDataSyncPayload payload = new S2CPlayerDataSyncPayload(
                player.getUUID(),
                professionalPlayer.getAllOccupations(),
                Optional.ofNullable(categoryId)
        );
        NetworkPayloadDispatcher.sendToPlayer(player, payload);
    }

    public static void syncActions(ServerPlayer player, IProfessionalPlayer professionalPlayer, PlayerManager playerManager) {
        S2CPlayerActionsSyncPayload payload = new S2CPlayerActionsSyncPayload(
                player.getUUID(),
                playerManager.getRelevantActionsForCategory(professionalPlayer.getCategory())
        );
        NetworkPayloadDispatcher.sendToPlayer(player, payload);
    }

    public static void syncPerks(ServerPlayer player, IProfessionalPlayer professionalPlayer, PlayerManager playerManager) {
        S2CPlayerPerksSyncPayload payload = new S2CPlayerPerksSyncPayload(
                player.getUUID(),
                playerManager.getAllPerksForPlayer(professionalPlayer)
        );
        NetworkPayloadDispatcher.sendToPlayer(player, payload);
    }

    public static void syncGates(ServerPlayer player, IProfessionalPlayer professionalPlayer, PlayerManager playerManager) {
        S2CPlayerGatesSyncPayload payload = new S2CPlayerGatesSyncPayload(
                player.getUUID(),
                playerManager.getRelevantGatesForPlayer(professionalPlayer)
        );
        NetworkPayloadDispatcher.sendToPlayer(player, payload);
    }
}

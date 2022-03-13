package com.epherical.professions;

import com.epherical.professions.api.ProfessionalPlayer;
import com.epherical.professions.data.Storage;
import com.google.common.collect.Maps;
import net.minecraft.server.level.ServerPlayer;

import java.util.Map;
import java.util.UUID;

public class PlayerManager {

    private final Map<UUID, ProfessionalPlayer> players = Maps.newHashMap();

    private final Storage<ProfessionalPlayer, UUID> storage;

    public PlayerManager(Storage<ProfessionalPlayer, UUID> storage) {
        this.storage = storage;
    }

    public void playerJoined(ServerPlayer player) {
        ProfessionalPlayer pPlayer = players.get(player.getUUID());
        if (pPlayer == null) {
            pPlayer = storage.getUser(player.getUUID());
            players.put(player.getUUID(), pPlayer);
        }
    }

    public void playerQuit(ServerPlayer player) {
        ProfessionalPlayer pPlayer = players.remove(player.getUUID());
        if (pPlayer != null) {
            pPlayer.save(storage);
        }
    }
}

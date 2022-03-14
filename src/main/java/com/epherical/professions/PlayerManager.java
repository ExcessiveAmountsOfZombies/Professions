package com.epherical.professions;

import com.epherical.professions.api.ProfessionalPlayer;
import com.epherical.professions.data.Storage;
import com.epherical.professions.profession.Profession;
import com.google.common.collect.Maps;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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

    public void playerJoinOccupation(ProfessionalPlayer player, Profession profession) {
        if (player.alreadyHasProfession(profession)) {
            return;
        }

        if (player)

    }

    @Nullable
    public ProfessionalPlayer getPlayer(@NotNull UUID uuid) {
        return players.get(uuid);
    }

    @Nullable
    public ProfessionalPlayer getPlayerOffline(@NotNull UUID uuid) {
        ProfessionalPlayer player = players.get(uuid);
        if (player == null) {
            player = storage.getUser(uuid);
        }
        return player;
    }
}

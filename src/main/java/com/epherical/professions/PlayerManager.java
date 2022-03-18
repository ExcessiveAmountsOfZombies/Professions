package com.epherical.professions;

import com.epherical.professions.api.ProfessionalPlayer;
import com.epherical.professions.config.ProfessionConfig;
import com.epherical.professions.data.Storage;
import com.epherical.professions.profession.Profession;
import com.epherical.professions.profession.progression.Occupation;
import com.epherical.professions.profession.progression.OccupationSlot;
import com.google.common.collect.Maps;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.Util;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.commands.SayCommand;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;
import java.util.Map;
import java.util.UUID;

public class PlayerManager {

    private final Map<UUID, ProfessionalPlayer> players = Maps.newHashMap();

    private final Storage<ProfessionalPlayer, UUID> storage;
    private final MinecraftServer server;

    public PlayerManager(Storage<ProfessionalPlayer, UUID> storage, MinecraftServer server) {
        this.storage = storage;
        this.server = server;
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

    public void joinOccupation(@Nullable ProfessionalPlayer player, @Nullable Profession profession, OccupationSlot slot, ServerPlayer serverPlayer) {
        if (profession == null) {
            serverPlayer.sendMessage(new TextComponent("Profession does not exist."), Util.NIL_UUID);
            return;
        }

        if (!hasPermission(serverPlayer, profession)) {
            serverPlayer.sendMessage(new TextComponent("no permission!!"), Util.NIL_UUID);
            return;
        }

        if (player == null) {
            return;
        }

        if (player.alreadyHasOccupation(profession) && player.isOccupationActive(profession)) {
            serverPlayer.sendMessage(new TextComponent("already joined occupation"), Util.NIL_UUID);
            return;
        }

        if (player.getActiveOccupations().size() >= ProfessionConfig.maxOccupations) {
            serverPlayer.sendMessage(new TextComponent("already joined max amount of occupations"), Util.NIL_UUID);
            return;
        }

        if (!player.joinOccupation(profession, slot)) {
            return;
        }
        serverPlayer.sendMessage(new TextComponent("You have joined an occupation!"), Util.NIL_UUID);

        storage.saveUser(player);
    }

    public boolean leaveOccupation(@Nullable ProfessionalPlayer player, @Nullable Profession profession, ServerPlayer serverPlayer) {
        if (profession == null) {
            serverPlayer.sendMessage(new TextComponent("That profession does not exist."), Util.NIL_UUID);
            return false;
        }

        if (player == null) {
            return false;
        }

        if (!player.leaveOccupation(profession)) {
            return false;
        }
        storage.saveUser(player);
        serverPlayer.sendMessage(new TextComponent("You have left an occupation!"), Util.NIL_UUID);
        return true;
    }

    /**
     * central method to send any announcements to the player or server about a player leveling up.
     */
    public void levelUp(ProfessionalPlayer player, Occupation occupation) {
        // TODO: make it translate
        TranslatableComponent message;
        ServerPlayer sPlayer = server.getPlayerList().getPlayer(player.getUuid());
        if (ProfessionConfig.announceLevelUps) {
            // todo: colors
            message = new TranslatableComponent("%s has leveled up %s to %s.", sPlayer.getDisplayName(), occupation.getProfession().getDisplayName(), occupation.getLevel());
        } else {
            message = new TranslatableComponent("You have leveled up %s to %s.", occupation.getProfession().getDisplayName(), occupation.getLevel());
        }

        if (ProfessionConfig.announceLevelUps) {
            server.getPlayerList().broadcastMessage(message, ChatType.SYSTEM, Util.NIL_UUID);
        } else {
            // todo: maybe multiple ways to send messages? hotbar, chat, toasts, title? for now it's just chat.
            sPlayer.sendMessage(message, Util.NIL_UUID);
        }

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

    public void reload() {
        if (server != null) {
            for (ServerPlayer player : server.getPlayerList().getPlayers()) {
                playerJoined(player);
            }
        }
    }

    private boolean hasPermission(ServerPlayer player, Profession profession) {
        String profKey = profession.getKey().toString().replaceAll(":", ".");
        return Permissions.check(player, "professions.join", 0) && Permissions.check(player, "professions.start." + profKey.toLowerCase(Locale.ROOT), 0);
    }
}

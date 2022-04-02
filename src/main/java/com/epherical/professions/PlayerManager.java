package com.epherical.professions;

import com.epherical.professions.api.ProfessionalPlayer;
import com.epherical.professions.config.ProfessionConfig;
import com.epherical.professions.data.Storage;
import com.epherical.professions.events.OccupationEvents;
import com.epherical.professions.profession.Profession;
import com.epherical.professions.profession.progression.Occupation;
import com.epherical.professions.profession.progression.OccupationSlot;
import com.google.common.collect.Maps;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.Util;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
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
            if (pPlayer != null) {
                pPlayer.setPlayer(player);
            }
            players.put(player.getUUID(), pPlayer);
        }
    }

    public void playerQuit(ServerPlayer player) {
        ProfessionalPlayer pPlayer = players.remove(player.getUUID());
        if (pPlayer != null) {
            pPlayer.setPlayer(null);
            pPlayer.save(storage);
        }
    }

    public void joinOccupation(@Nullable ProfessionalPlayer player, @Nullable Profession profession, OccupationSlot slot, ServerPlayer serverPlayer) {
        if (profession == null) {
            serverPlayer.sendMessage(new TranslatableComponent("professions.command.join.error.does_not_exist").setStyle(Style.EMPTY.withColor(ProfessionConfig.errors)), Util.NIL_UUID);
            return;
        }

        if (!hasPermission(serverPlayer, profession)) {
            serverPlayer.sendMessage(new TranslatableComponent("professions.command.join.error.no_permission").setStyle(Style.EMPTY.withColor(ProfessionConfig.errors)), Util.NIL_UUID);
            return;
        }

        if (player == null) {
            return;
        }

        if (player.alreadyHasOccupation(profession) && player.isOccupationActive(profession)) {
            serverPlayer.sendMessage(new TranslatableComponent("professions.command.join.error.already_joined").setStyle(Style.EMPTY.withColor(ProfessionConfig.errors)), Util.NIL_UUID);
            return;
        }

        if (player.getActiveOccupations().size() >= ProfessionConfig.maxOccupations) {
            serverPlayer.sendMessage(new TranslatableComponent("professions.command.join.error.max_occupations").setStyle(Style.EMPTY.withColor(ProfessionConfig.errors)), Util.NIL_UUID);
            return;
        }

        if (!player.joinOccupation(profession, slot)) {
            return;
        }
        OccupationEvents.PROFESSION_JOIN_EVENT.invoker().onProfessionJoin(player, profession, slot, player.getPlayer());
        serverPlayer.sendMessage(new TranslatableComponent("professions.command.join.success", profession.getDisplayComponent()).setStyle(Style.EMPTY.withColor(ProfessionConfig.success)), Util.NIL_UUID);

        storage.saveUser(player);
    }

    public boolean leaveOccupation(@Nullable ProfessionalPlayer player, @Nullable Profession profession, ServerPlayer serverPlayer) {
        if (profession == null) {
            serverPlayer.sendMessage(new TranslatableComponent("professions.command.leave.error.does_not_exist").setStyle(Style.EMPTY.withColor(ProfessionConfig.errors)), Util.NIL_UUID);
            return false;
        }

        if (player == null) {
            return false;
        }

        if (!player.leaveOccupation(profession)) {
            return false;
        }
        OccupationEvents.PROFESSION_LEAVE_EVENT.invoker().onProfessionLeave(player, profession, serverPlayer);
        storage.saveUser(player);
        serverPlayer.sendMessage(new TranslatableComponent("professions.command.leave.success", profession.getDisplayComponent()).setStyle(Style.EMPTY.withColor(ProfessionConfig.success)), Util.NIL_UUID);
        return true;
    }

    public boolean fireFromOccupation(@Nullable ProfessionalPlayer player, @Nullable Profession profession, ServerPlayer serverPlayer) {
        if (profession == null) {
            serverPlayer.sendMessage(new TranslatableComponent("professions.command.fire.error.does_not_exist").setStyle(Style.EMPTY.withColor(ProfessionConfig.errors)), Util.NIL_UUID);
            return false;
        }

        if (player == null) {
            serverPlayer.sendMessage(new TranslatableComponent("professions.command.fire.error.cant_find_player").setStyle(Style.EMPTY.withColor(ProfessionConfig.errors)), Util.NIL_UUID);
            return false;
        }

        if (player.fireFromOccupation(profession)) {
            storage.saveUser(player);
            return true;
        } else {
            return false;
        }
    }

    /**
     * central method to send any announcements to the player or server about a player leveling up.
     */
    public void levelUp(ProfessionalPlayer player, Occupation occupation) {
        MutableComponent message;
        ServerPlayer sPlayer = server.getPlayerList().getPlayer(player.getUuid());
        if (sPlayer == null) {
            return; // this probably won't happen, but if it does, no NPEs.
        }
        if (((ProfessionConfig.announceEveryXLevel % occupation.getLevel() == 0)) && ProfessionConfig.announceLevelUps) {
            message = new TranslatableComponent("professions.level_up.announcement",
                    sPlayer.getDisplayName(),
                    occupation.getProfession().getDisplayComponent(),
                    new TextComponent("" + occupation.getLevel()).setStyle(Style.EMPTY.withColor(ProfessionConfig.variables)))
                    .setStyle(Style.EMPTY.withColor(ProfessionConfig.success));
            server.getPlayerList().broadcastMessage(message, ChatType.SYSTEM, Util.NIL_UUID);
        } else {
            message = new TranslatableComponent("professions.level_up.local",
                    occupation.getProfession().getDisplayComponent(),
                    new TextComponent("" + occupation.getLevel()).setStyle(Style.EMPTY.withColor(ProfessionConfig.variables)))
                    .setStyle(Style.EMPTY.withColor(ProfessionConfig.success));
            sPlayer.sendMessage(message, Util.NIL_UUID);
        }
    }

    @Nullable
    public ProfessionalPlayer getPlayer(@NotNull UUID uuid) {
        ProfessionalPlayer player = players.get(uuid);
        if (player == null) {
            player = storage.getUser(uuid);
        }
        return player;
    }

    public Collection<ProfessionalPlayer> getPlayers() {
        return players.values();
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

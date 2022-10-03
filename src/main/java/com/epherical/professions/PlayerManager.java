package com.epherical.professions;

import com.epherical.professions.api.ProfessionalPlayer;
import com.epherical.professions.config.ProfessionConfig;
import com.epherical.professions.data.Storage;
import com.epherical.professions.profession.Profession;
import com.epherical.professions.profession.progression.Occupation;
import com.epherical.professions.profession.progression.OccupationSlot;
import com.epherical.professions.profession.progression.ProfessionalPlayerImpl;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import net.minecraft.Util;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class PlayerManager {

    private final Map<UUID, ProfessionalPlayer> players = Maps.newHashMap();
    private final Set<UUID> synchronizedPlayers = Sets.newHashSet();

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
                pPlayer.updateOccupationPerks();
                player.setHealth(player.getHealth());
            }
            players.put(player.getUUID(), pPlayer);
        }
        CommonPlatform.platform.sendSyncRequest(player);
    }

    public void playerQuit(ServerPlayer player) {
        ProfessionalPlayer pPlayer = players.remove(player.getUUID());
        synchronizedPlayers.remove(player.getUUID());
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

        if (ProfessionConfig.maxOccupations != 0 && player.getActiveOccupations().size() >= ProfessionConfig.maxOccupations) {
            serverPlayer.sendMessage(new TranslatableComponent("professions.command.join.error.max_occupations").setStyle(Style.EMPTY.withColor(ProfessionConfig.errors)), Util.NIL_UUID);
            return;
        }

        if (!player.joinOccupation(profession, slot)) {
            return;
        }
        CommonPlatform.platform.professionJoinEvent(player, profession, slot, player.getPlayer());
        serverPlayer.sendMessage(new TranslatableComponent("professions.command.join.success", profession.getDisplayComponent()).setStyle(Style.EMPTY.withColor(ProfessionConfig.success)), Util.NIL_UUID);

        storage.saveUser(player);
    }

    public boolean leaveOccupation(@Nullable ProfessionalPlayer player, @Nullable Profession profession, ServerPlayer serverPlayer) {
        if (profession == null) {
            serverPlayer.sendMessage(new TranslatableComponent("professions.command.leave.error.does_not_exist").setStyle(Style.EMPTY.withColor(ProfessionConfig.errors)), Util.NIL_UUID);
            return false;
        }

        if (CommonPlatform.platform.isClientEnvironment() && ProfessionConfig.preventLeavingProfession && CommonPlatform.platform.checkPermission(serverPlayer, "professions.bypass.leave_prevention")) {
            // todo translations
            serverPlayer.sendMessage(new TranslatableComponent("Allowing you to leave profession, but only because cheats are enabled.").setStyle(Style.EMPTY.withColor(ProfessionConfig.success)), Util.NIL_UUID);
            return false;

        }

        if (ProfessionConfig.preventLeavingProfession && !CommonPlatform.platform.checkPermission(serverPlayer, "professions.bypass.leave_prevention")) {
            serverPlayer.sendMessage(new TranslatableComponent("professions.command.leave.error.disabled_in_config").setStyle(Style.EMPTY.withColor(ProfessionConfig.errors)), Util.NIL_UUID);
            return false;
        }

        if (player == null) {
            return false;
        }

        if (!player.leaveOccupation(profession)) {
            return false;
        }
        CommonPlatform.platform.professionLeaveEvent(player, profession, serverPlayer);
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
    public void levelUp(ProfessionalPlayer player, Occupation occupation, int oldLevel) {
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
        List<Component> components = new ArrayList<>();
        // TODO: size limit
        occupation.getData().getUnlockables()
                .stream()
                .filter(singular -> singular.canUse(player))
                .forEach(singular -> components.add(singular.createUnlockComponent()));
        if (components.size() > 0) {
            MutableComponent megaComponent = new TranslatableComponent("professions.level_up.rewards").append("\n").setStyle(Style.EMPTY.withColor(ProfessionConfig.headerBorders));
            for (Component component : components) {
                megaComponent.append("  ").append(component).append("\n");
            }
            megaComponent.append(new TextComponent("=-=-=-=-=-=-=")).setStyle(Style.EMPTY.withColor(ProfessionConfig.headerBorders));
            MutableComponent unlockMessage = new TranslatableComponent("professions.level_up.unlock",
                            new TextComponent(String.valueOf(components.size())).setStyle(Style.EMPTY.withColor(ProfessionConfig.variables)))
                    .setStyle(Style.EMPTY.withColor(ProfessionConfig.descriptors)
                            .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, megaComponent)));
            sPlayer.sendMessage(unlockMessage, Util.NIL_UUID);
        }
    }

    @Nullable
    public ProfessionalPlayer getPlayer(@NotNull UUID uuid) {
        ProfessionalPlayer player = players.get(uuid);
        if (player == null && storage != null) {
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
            synchronizedPlayers.clear();
            for (ServerPlayer player : server.getPlayerList().getPlayers()) {
                playerQuit(player);
                playerJoined(player);
            }
        }
    }

    private boolean hasPermission(ServerPlayer player, Profession profession) {
        String profKey = profession.getKey().toString().replaceAll(":", ".");
        return CommonPlatform.platform.checkPermission(player, "professions.join", 0) &&
                CommonPlatform.platform.checkDynamicPermission(player, "professions.start", profKey.toLowerCase(Locale.ROOT), 0);
    }

    public List<Occupation> synchronizePlayer(ServerPlayer player) {
        if (synchronizedPlayers.contains(player.getUUID())) {
            return Collections.emptyList(); // don't need to send the data if it's already been sent and nothing has changed.
        }
        synchronizedPlayers.add(player.getUUID());
        return players.get(player.getUUID()).getActiveOccupations();
    }

    public boolean isSynchronized(ServerPlayer player) {
        return synchronizedPlayers.contains(player.getUUID());
    }

    public boolean isSynchronized(UUID uuid) {
        return synchronizedPlayers.contains(uuid) || CommonPlatform.platform.isClientEnvironment();
    }


    /**
     * Only call on the CLIENT
     */
    public void addClientPlayer(UUID player, List<Occupation> occupationList) {
        players.put(player, new ProfessionalPlayerImpl(occupationList));
    }

    /**
     * Only call on the CLIENT
     * @param uuid playerUUID
     */
    public void playerClientQuit(UUID uuid) {
        players.remove(uuid);
    }
}

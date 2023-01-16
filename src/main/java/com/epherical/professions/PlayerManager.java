package com.epherical.professions;

import com.epherical.professions.api.ProfessionalPlayer;
import com.epherical.professions.config.ProfessionConfig;
import com.epherical.professions.data.Storage;
import com.epherical.professions.profession.Profession;
import com.epherical.professions.profession.progression.Occupation;
import com.epherical.professions.profession.progression.OccupationSlot;
import com.epherical.professions.profession.progression.ProfessionalPlayerImpl;
import com.epherical.professions.util.ActionLogger;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
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
        ProfessionPlatform.platform.sendSyncRequest(player);
    }

    public void playerQuit(ServerPlayer player) {
        ProfessionalPlayer pPlayer = players.remove(player.getUUID());
        synchronizedPlayers.remove(player.getUUID());
        ActionLogger.removePlayerXpRateOutput(player.getUUID());
        if (pPlayer != null) {
            pPlayer.setPlayer(null);
            pPlayer.save(storage);
        }
    }

    public void joinOccupation(@Nullable ProfessionalPlayer player, @Nullable Profession profession, OccupationSlot slot, ServerPlayer serverPlayer) {
        if (profession == null) {
            serverPlayer.sendSystemMessage(Component.translatable("professions.command.join.error.does_not_exist").setStyle(Style.EMPTY.withColor(ProfessionConfig.errors)));
            return;
        }

        if (!hasPermission(serverPlayer, profession)) {
            serverPlayer.sendSystemMessage(Component.translatable("professions.command.join.error.no_permission").setStyle(Style.EMPTY.withColor(ProfessionConfig.errors)));
            return;
        }

        if (player == null) {
            return;
        }

        if (player.alreadyHasOccupation(profession) && player.isOccupationActive(profession)) {
            serverPlayer.sendSystemMessage(Component.translatable("professions.command.join.error.already_joined").setStyle(Style.EMPTY.withColor(ProfessionConfig.errors)));
            return;
        }

        if (ProfessionConfig.maxOccupations != 0 && player.getActiveOccupations().size() >= ProfessionConfig.maxOccupations) {
            serverPlayer.sendSystemMessage(Component.translatable("professions.command.join.error.max_occupations").setStyle(Style.EMPTY.withColor(ProfessionConfig.errors)));
            return;
        }

        if (!player.joinOccupation(profession, slot)) {
            return;
        }
        ProfessionPlatform.platform.professionJoinEvent(player, profession, slot, player.getPlayer());
        serverPlayer.sendSystemMessage(Component.translatable("professions.command.join.success", profession.getDisplayComponent()).setStyle(Style.EMPTY.withColor(ProfessionConfig.success)));

        storage.saveUser(player);
    }

    public boolean leaveOccupation(@Nullable ProfessionalPlayer player, @Nullable Profession profession, ServerPlayer serverPlayer) {
        if (profession == null) {
            serverPlayer.sendSystemMessage(Component.translatable("professions.command.leave.error.does_not_exist").setStyle(Style.EMPTY.withColor(ProfessionConfig.errors)));
            return false;
        }

        if (ProfessionPlatform.platform.isClientEnvironment() && ProfessionConfig.preventLeavingProfession && ProfessionPlatform.platform.checkPermission(serverPlayer, "professions.bypass.leave_prevention")) {
            // todo translations
            serverPlayer.sendSystemMessage(Component.translatable("Allowing you to leave profession, but only because cheats are enabled.").setStyle(Style.EMPTY.withColor(ProfessionConfig.success)));
            return false;

        }

        if (ProfessionConfig.preventLeavingProfession && !ProfessionPlatform.platform.checkPermission(serverPlayer, "professions.bypass.leave_prevention")) {
            serverPlayer.sendSystemMessage(Component.translatable("professions.command.leave.error.disabled_in_config").setStyle(Style.EMPTY.withColor(ProfessionConfig.errors)));
            return false;
        }

        if (player == null) {
            return false;
        }

        if (!player.leaveOccupation(profession)) {
            return false;
        }
        ProfessionPlatform.platform.professionLeaveEvent(player, profession, serverPlayer);
        storage.saveUser(player);
        serverPlayer.sendSystemMessage(Component.translatable("professions.command.leave.success", profession.getDisplayComponent()).setStyle(Style.EMPTY.withColor(ProfessionConfig.success)));
        return true;
    }

    public boolean fireFromOccupation(@Nullable ProfessionalPlayer player, @Nullable Profession profession, ServerPlayer serverPlayer) {
        if (profession == null) {
            serverPlayer.sendSystemMessage(Component.translatable("professions.command.fire.error.does_not_exist").setStyle(Style.EMPTY.withColor(ProfessionConfig.errors)));
            return false;
        }

        if (player == null) {
            serverPlayer.sendSystemMessage(Component.translatable("professions.command.fire.error.cant_find_player").setStyle(Style.EMPTY.withColor(ProfessionConfig.errors)));
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
            message = Component.translatable("professions.level_up.announcement",
                            sPlayer.getDisplayName(),
                            occupation.getProfession().getDisplayComponent(),
                            Component.literal("" + occupation.getLevel()).setStyle(Style.EMPTY.withColor(ProfessionConfig.variables)))
                    .setStyle(Style.EMPTY.withColor(ProfessionConfig.success));
            server.getPlayerList().broadcastSystemMessage(message, false);
        } else {
            message = Component.translatable("professions.level_up.local",
                            occupation.getProfession().getDisplayComponent(),
                            Component.literal("" + occupation.getLevel()).setStyle(Style.EMPTY.withColor(ProfessionConfig.variables)))
                    .setStyle(Style.EMPTY.withColor(ProfessionConfig.success));
            sPlayer.sendSystemMessage(message);
        }
        List<Component> components = new ArrayList<>();
        // TODO: size limit
        occupation.getData().getUnlockables()
                .stream()
                .filter(singular -> singular.canUse(player))
                .forEach(singular -> components.add(singular.createUnlockComponent()));
        if (components.size() > 0) {
            MutableComponent megaComponent = Component.translatable("professions.level_up.rewards").append("\n").setStyle(Style.EMPTY.withColor(ProfessionConfig.headerBorders));
            for (Component component : components) {
                megaComponent.append("  ").append(component).append("\n");
            }
            megaComponent.append(Component.literal("=-=-=-=-=-=-=")).setStyle(Style.EMPTY.withColor(ProfessionConfig.headerBorders));
            MutableComponent unlockMessage = Component.translatable("professions.level_up.unlock",
                            Component.literal(String.valueOf(components.size())).setStyle(Style.EMPTY.withColor(ProfessionConfig.variables)))
                    .setStyle(Style.EMPTY.withColor(ProfessionConfig.descriptors)
                            .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, megaComponent)));
            sPlayer.sendSystemMessage(unlockMessage);
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
        return ProfessionPlatform.platform.checkPermission(player, "professions.join", 0) &&
                ProfessionPlatform.platform.checkDynamicPermission(player, "professions.start", profKey.toLowerCase(Locale.ROOT), 0);
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
        return synchronizedPlayers.contains(uuid) || ProfessionPlatform.platform.isClientEnvironment();
    }


    /**
     * Only call on the CLIENT
     */
    public void addClientPlayer(UUID player, List<Occupation> occupationList) {
        players.put(player, new ProfessionalPlayerImpl(occupationList));
    }

    /**
     * Only call on the CLIENT
     *
     * @param uuid playerUUID
     */
    public void playerClientQuit(UUID uuid) {
        players.remove(uuid);
    }
}

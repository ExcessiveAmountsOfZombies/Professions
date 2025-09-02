package com.epherical.professions;

import com.epherical.professions.api.IProfessionalPlayer;
import com.epherical.professions.config.ProfessionConfig;
import com.epherical.professions.core.progression.Occupation;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import net.minecraft.network.chat.Component;
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
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;

public class PlayerManager {

    private final Map<UUID, IProfessionalPlayer> players = Maps.newHashMap();
    private final Set<UUID> synchronizedPlayers = Sets.newHashSet();

    private final Function<ServerPlayer, IProfessionalPlayer> playerFactory;

    private final MinecraftServer server;

    public PlayerManager(MinecraftServer server, Function<ServerPlayer, IProfessionalPlayer> playerFactory) {
        this.server = server;
        this.playerFactory = playerFactory;
    }

    public void playerJoined(ServerPlayer player) {
        IProfessionalPlayer pPlayer = players.get(player.getUUID());
        if (pPlayer == null) {
            pPlayer = playerFactory.apply(player);
            if (pPlayer != null) {
                pPlayer.setPlayer(player);
                pPlayer.updateOccupationPerks();
                player.setHealth(player.getHealth());
            }
            players.put(player.getUUID(), pPlayer);
        }
        // todo
        //ProfessionPlatform.platform.sendSyncRequest(player);
    }

    public void playerQuit(ServerPlayer player) {
        IProfessionalPlayer pPlayer = players.remove(player.getUUID());
        synchronizedPlayers.remove(player.getUUID());
        if (pPlayer != null) {
            pPlayer.setPlayer(null);
            pPlayer.save();
        }
    }

    /*public void joinOccupation(@Nullable IProfessionalPlayer player, @Nullable Profession profession, OccupationSlot slot, ServerPlayer serverPlayer) {
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
    }*/

    /*public boolean leaveOccupation(@Nullable IProfessionalPlayer player, @Nullable Profession profession, ServerPlayer serverPlayer) {
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
    }*/

    /*public boolean fireFromOccupation(@Nullable IProfessionalPlayer player, @Nullable Profession profession, ServerPlayer serverPlayer) {
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
    }*/

    /**
     * central method to send any announcements to the player or server about a player leveling up.
     */
    public void levelUp(IProfessionalPlayer player, Occupation occupation, int oldLevel) {
        MutableComponent message;
        // todo; fix this message
        ServerPlayer sPlayer = server.getPlayerList().getPlayer(player.getUUID());
        if (sPlayer == null) {
            return; // this probably won't happen, but if it does, no NPEs.
        }
        if (((ProfessionConfig.announceEveryXLevel % occupation.getLevel() == 0)) && ProfessionConfig.announceLevelUps) {
            message = Component.translatable("professions.level_up.announcement",
                            sPlayer.getDisplayName(),
                            occupation.getProfession().value().displayName(),
                            Component.literal("" + occupation.getLevel()).setStyle(Style.EMPTY.withColor(ProfessionConfig.variables)))
                    .setStyle(Style.EMPTY.withColor(ProfessionConfig.success));
            server.getPlayerList().broadcastSystemMessage(message, false);
        } else {
            message = Component.translatable("professions.level_up.local",
                            occupation.getProfession().value().displayName(),
                            Component.literal("" + occupation.getLevel()).setStyle(Style.EMPTY.withColor(ProfessionConfig.variables)))
                    .setStyle(Style.EMPTY.withColor(ProfessionConfig.success));
            sPlayer.sendSystemMessage(message);
        }
        List<Component> components = new ArrayList<>();
        // TODO: size limit
        /*occupation.getData().getUnlockables()
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
        }*/
    }

    @Nullable
    public IProfessionalPlayer getPlayer(@NotNull ServerPlayer serverPlayer) {
        IProfessionalPlayer player = players.get(serverPlayer.getUUID());
        if (player == null) {
            player = playerFactory.apply(serverPlayer);
        }
        return player;
    }

    public Collection<IProfessionalPlayer> getPlayers() {
        return players.values();
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
        return synchronizedPlayers.contains(uuid) || CommonClass.INSTANCE.isClientEnvironment();
    }
}

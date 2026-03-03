package com.epherical.professions;

import com.epherical.professions.api.IProfessionalPlayer;
import com.epherical.professions.core.actions.Action;
import com.epherical.professions.core.actions.ActionType;
import com.epherical.professions.core.context.ProfessionContext;
import com.epherical.professions.core.context.ProfessionParameter;
import com.epherical.professions.core.progression.Occupation;
import com.epherical.professions.core.progression.ProfessionalPlayer;
import com.epherical.professions.data.config.ProfessionConfig;
import com.epherical.professions.data.player.OccupationDataLoader;
import com.google.common.collect.Maps;
import com.mojang.authlib.GameProfile;
import com.mojang.logging.LogUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class PlayerManager {

    private static final Logger LOGGER = LogUtils.getLogger();

    private final Map<UUID, IProfessionalPlayer> players = Maps.newHashMap();
    private final Map<UUID, String> uuidToUsername = Maps.newHashMap();


    private final ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);


    private MinecraftServer server;

    private final ActionManager actionManager;
    private OccupationDataLoader occupationDataLoader;

    public PlayerManager(MinecraftServer server, ActionManager actionManager, OccupationDataLoader loader) {
        this.server = server;
        this.actionManager = actionManager;
        this.occupationDataLoader = loader;
        executor.scheduleAtFixedRate(this::saveAll, 5, 5, TimeUnit.MINUTES);
    }

    public void loadAll() {
        occupationDataLoader.loadAll().thenCompose(all -> {
            players.clear();
            uuidToUsername.clear();


            for (Map.Entry<UUID, List<Occupation>> entry : all.entrySet()) {
                UUID uuid = entry.getKey();
                players.put(uuid, new ProfessionalPlayer(uuid, entry.getValue()));

                Optional<GameProfile> gameProfile = server.getProfileCache().get(uuid);
                if (gameProfile.isPresent()) {
                    uuidToUsername.put(uuid, gameProfile.get().getName());
                } else {
                    uuidToUsername.put(uuid, uuid.toString());
                }
            }

            return CompletableFuture.completedFuture(null);
        });
    }

    public CompletableFuture<Void> saveAll() {
        List<CompletableFuture<Void>> saves = new ArrayList<>();

        for (Map.Entry<UUID, IProfessionalPlayer> entry : players.entrySet()) {
            UUID uuid = entry.getKey();
            IProfessionalPlayer player = entry.getValue();
            if (player.isDirty()) {
                saves.add(occupationDataLoader.save(uuid, player.getAllOccupations()));
                player.setDirty(false);
            }

        }

        return CompletableFuture.allOf(saves.toArray(new CompletableFuture[0]));
    }

    public void shutdown() {
        List<Runnable> runnables = executor.shutdownNow();
        runnables.forEach(Runnable::run);
        LOGGER.info("All players were saved before shutdown completed.");
    }

    public void playerJoined(ServerPlayer player) {

        IProfessionalPlayer pPlayer = players.get(player.getUUID());
        if (pPlayer != null) {
            pPlayer.setPlayer(player);
        } else {
            // probably new player
            CompletableFuture<List<Occupation>> load = occupationDataLoader.load(player.getUUID());
            List<Occupation> join = load.join();
            pPlayer = new ProfessionalPlayer(join, player, server.registryAccess());
            players.put(player.getUUID(), pPlayer);
            LOGGER.debug("New player joined! Assigned professions data {}", player.getUUID());
        }
    }

    public void playerQuit(ServerPlayer player) {
        UUID uuid = player.getUUID();
        IProfessionalPlayer pPlayer = players.get(uuid);
        CompletableFuture<Void> save = occupationDataLoader.save(uuid, pPlayer.getAllOccupations());
        save.thenAccept(a -> LOGGER.debug("Player {} saved their professions data", uuid));
        pPlayer.setPlayer(null);
    }

    public void processAction(Player player, ActionType actionType, ProfessionContext.Builder context) {
        IProfessionalPlayer iProfessionalPlayer = players.get(player.getUUID());

        context.addParameter(ProfessionParameter.THIS_PLAYER, iProfessionalPlayer);

        ProfessionContext professionContext = context.build();


        Collection<Action<?>> actions = actionManager.getActionsByType(actionType);

        for (Action<?> action : actions) {
            Occupation occupation = iProfessionalPlayer.getOccupation(action.getProfession());
            if (occupation.isActive() && action.isValidAction(professionContext)) {
                action.handleAction(professionContext, occupation);
            }
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

    public Collection<IProfessionalPlayer> getPlayers() {
        return players.values();
    }

    public IProfessionalPlayer getPlayer(UUID uuid) {
        return players.get(uuid);
    }

    public String getPlayerNameFromUUID(UUID uuid) {
        return uuidToUsername.get(uuid);
    }

    public void setServer(MinecraftServer server) {
        this.server = server;
    }

    public void setOccupationDataLoader(OccupationDataLoader occupationDataLoader) {
        this.occupationDataLoader = occupationDataLoader;
    }

}

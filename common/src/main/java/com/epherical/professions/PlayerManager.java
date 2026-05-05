package com.epherical.professions;

import com.epherical.professions.api.IProfessionalPlayer;
import com.epherical.professions.api.event.runtime.ActionProcessingEvent;
import com.epherical.professions.api.event.runtime.ActionValidEvent;
import com.epherical.professions.api.event.runtime.ProfessionEventBus;
import com.epherical.professions.api.event.runtime.rewards.RewardEvent;
import com.epherical.professions.core.Profession;
import com.epherical.professions.core.ProfessionCategory;
import com.epherical.professions.core.context.ProfessionContext;
import com.epherical.professions.core.context.ProfessionParameter;
import com.epherical.professions.data.player.OccupationDataLoader;
import com.epherical.professions.data.player.PlayerOccupationData;
import com.epherical.professions.domain.exception.ProfessionNotActiveException;
import com.epherical.professions.model.Occupation;
import com.epherical.professions.model.ProfessionalPlayer;
import com.epherical.professions.model.actions.Action;
import com.epherical.professions.model.actions.rewards.Reward;
import com.google.common.collect.Maps;
import com.mojang.authlib.GameProfile;
import com.mojang.logging.LogUtils;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static com.epherical.professions.ProfessionsCommon.PROFESSION_REGISTRY_KEY;

public class PlayerManager {

    private static final Logger LOGGER = LogUtils.getLogger();

    private final Map<UUID, IProfessionalPlayer> players = Maps.newHashMap();
    private final Map<UUID, String> uuidToUsername = Maps.newHashMap();


    private ScheduledExecutorService executor;


    private MinecraftServer server;

    private final ActionManager actionManager;
    private OccupationDataLoader occupationDataLoader;
    private final ProfessionCategoryManager categoryManager;

    private final ProfessionEventBus eventBus;

    public PlayerManager(ActionManager actionManager, OccupationDataLoader loader, ProfessionEventBus eventBus, ProfessionCategoryManager categoryManager) {
        this.actionManager = actionManager;
        this.occupationDataLoader = loader;
        this.eventBus = eventBus;
        this.categoryManager = categoryManager;
    }

    public void startExecutor() {
        executor = Executors.newScheduledThreadPool(1);
        executor.scheduleAtFixedRate(this::saveAll, 5, 5, TimeUnit.MINUTES);
    }

    public void loadAll() {
        occupationDataLoader.loadAll().thenCompose(all -> {
            players.clear();
            uuidToUsername.clear();


            for (Map.Entry<UUID, PlayerOccupationData> entry : all.entrySet()) {
                UUID uuid = entry.getKey();
                PlayerOccupationData data = entry.getValue();
                ProfessionalPlayer player = new ProfessionalPlayer(uuid, data.occupations());
                applyCategoryData(player, data.professionCategoryId(), uuid);
                players.put(uuid, player);

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
                saves.add(occupationDataLoader.save(uuid, player.getAllOccupations(), getCategoryId(player)));
                player.markDirty(false);
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
        // todo; fire event for new players that joined.

        IProfessionalPlayer pPlayer = players.get(player.getUUID());
        if (pPlayer != null) {
            pPlayer.setPlayer(player);
        } else {
            // probably new player
            CompletableFuture<PlayerOccupationData> load = occupationDataLoader.load(player.getUUID());
            PlayerOccupationData join = load.join();
            ProfessionalPlayer professionalPlayer = new ProfessionalPlayer(join.occupations(), player, server.registryAccess());
            applyCategoryData(professionalPlayer, join.professionCategoryId(), player.getUUID());
            pPlayer = professionalPlayer;
            players.put(player.getUUID(), pPlayer);
            LOGGER.debug("New player joined! Assigned professions data {}", player.getUUID());
        }
    }

    public void playerQuit(ServerPlayer player) {
        // todo; fire an event
        UUID uuid = player.getUUID();
        IProfessionalPlayer pPlayer = players.get(uuid);
        CompletableFuture<Void> save = occupationDataLoader.save(uuid, pPlayer.getAllOccupations(), getCategoryId(pPlayer));
        save.thenAccept(a -> LOGGER.debug("Player {} saved their professions data", uuid));
        pPlayer.setPlayer(null);
    }

    public void processAction(Player player, ProfessionContext professionContext) {
        IProfessionalPlayer iProfessionalPlayer = professionContext.getParameter(ProfessionParameter.THIS_PLAYER);
        Collection<Action<?>> actions = actionManager.getActionsByType(professionContext.getParameter(ProfessionParameter.ACTION_TYPE));

        if (iProfessionalPlayer.getCategory() == null) {
            // todo; send a message to the player telling them to select a category
            return; // do nothing.
        }

        ActionProcessingEvent processingEvent = new ActionProcessingEvent(actions, iProfessionalPlayer, professionContext);
        eventBus.post(processingEvent);
        if (processingEvent.isCanceled()) {
            return; // do nothing.
        }

        for (Action<?> action : actions) {
            Occupation occupation = iProfessionalPlayer.getOccupation(action.getProfession());
            if (occupation == null || !occupation.isActive() || !iProfessionalPlayer.getCategory().hasProfession(action.getProfession())) continue;

            ActionValidEvent actionValidEvent = new ActionValidEvent(action, occupation, iProfessionalPlayer, professionContext);

            if (action.isValidAction(professionContext)) {
                eventBus.post(actionValidEvent);
                if (!actionValidEvent.isCanceled()) {
                    for (Reward<?> reward : action.getRewards()) {
                        testReward(reward, action, occupation, professionContext);
                    }
                }
            }
        }
    }

    public Collection<IProfessionalPlayer> getPlayers() {
        return players.values();
    }

    private void testReward(Reward<?> reward, Action<?> action, Occupation occupation, ProfessionContext professionContext) {
        applyRewardTyped(reward, action, occupation, professionContext);
    }

    private <T extends RewardEvent> void applyRewardTyped(Reward<T> reward, Action<?> action, Occupation occupation, ProfessionContext professionContext) {
        T rewardEvent = reward.buildEvent(occupation, action, professionContext);
        eventBus.post(rewardEvent);
        if (!rewardEvent.isCanceled()) {
            reward.giveReward(rewardEvent);
        }
    }

    @Nullable
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

    public @Nullable ResourceLocation getCategoryIdFor(IProfessionalPlayer player) {
        return getCategoryId(player);
    }

    public List<Action<?>> getRelevantActionsForCategory(@Nullable ProfessionCategory category) {
        if (category == null || server == null) {
            return List.of();
        }

        Optional<HolderLookup.RegistryLookup<Profession>> lookupOptional = server.registryAccess().lookup(PROFESSION_REGISTRY_KEY);
        if (lookupOptional.isEmpty()) {
            return List.of();
        }

        HolderLookup.RegistryLookup<Profession> lookup = lookupOptional.get();
        Set<Action<?>> relevantActions = new LinkedHashSet<>();

        for (ResourceKey<Profession> professionKey : category.professions()) {
            lookup.get(professionKey).ifPresent(holder -> relevantActions.addAll(actionManager.getActionsByProfession(holder)));
        }

        return List.copyOf(relevantActions);
    }

    public void applyClientSync(UUID playerId, List<Occupation> occupations, @Nullable ResourceLocation categoryId,
                                List<Action<?>> actions, @Nullable RegistryAccess registryAccess) {
        for (Occupation occupation : occupations) {
            occupation.resolveProfession(registryAccess);
        }

        ProfessionalPlayer player = new ProfessionalPlayer(playerId, occupations);
        applyCategoryData(player, categoryId, playerId);
        players.put(playerId, player);

        if (registryAccess != null) {
            actionManager.setRegistryLookup(registryAccess);
        }
        actionManager.reloadActions(actions);
    }

    public void applyClientExperienceGain(UUID playerId, ResourceLocation professionId, double gainedExperience, @Nullable RegistryAccess registryAccess) {
        IProfessionalPlayer professionalPlayer = players.get(playerId);
        if (professionalPlayer == null || registryAccess == null) {
            return;
        }

        Optional<Holder.Reference<Profession>> professionReference = registryAccess.lookupOrThrow(PROFESSION_REGISTRY_KEY).get(ResourceKey.create(PROFESSION_REGISTRY_KEY, professionId));
        if (professionReference.isPresent()) {
            Holder.Reference<Profession> professionReference1 = professionReference.get();
            Occupation occupation = professionalPlayer.getOccupation(professionReference1);
            if (occupation == null) {
                return;
            }
            //occupation.resolveProfession(registryAccess);
            try {
                occupation.addExp(gainedExperience, professionalPlayer);
            } catch (ProfessionNotActiveException exception) {
                LOGGER.debug("Unable to apply client EXP sync for profession {}", professionId, exception);
            }
        }
    }

    private void applyCategoryData(ProfessionalPlayer player, @Nullable ResourceLocation categoryId, UUID uuid) {
        if (categoryId == null) {
            return;
        }

        ProfessionCategory category = categoryManager.getCategory(categoryId);
        if (category == null) {
            LOGGER.warn("Player {} has unknown profession category id {} in saved data.", uuid, categoryId);
            return;
        }

        player.setCategory(category);
    }

    private @Nullable ResourceLocation getCategoryId(IProfessionalPlayer player) {
        ProfessionCategory category = player.getCategory();
        if (category == null) {
            return null;
        }
        return categoryManager.getCategoryId(category);
    }

}

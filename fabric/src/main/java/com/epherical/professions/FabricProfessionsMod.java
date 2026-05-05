package com.epherical.professions;

import com.epherical.professions.api.IProfessionalPlayer;
import com.epherical.professions.bootstrap.Actions;
import com.epherical.professions.core.Profession;
import com.epherical.professions.core.context.ProfessionContext;
import com.epherical.professions.core.context.ProfessionParameter;
import com.epherical.professions.core.register.PlatformBootstrap;
import com.epherical.professions.data.player.UuidOccupationDataLoader;
import com.epherical.professions.model.actions.ActionType;
import com.epherical.professions.model.actions.conditions.ConditionType;
import com.epherical.professions.model.actions.rewards.RewardType;
import com.epherical.professions.networking.NetworkPayloadDispatcher;
import com.epherical.professions.networking.client.C2SCategorySelectionPayload;
import com.epherical.professions.networking.server.CategorySelectionPayloadHandler;
import com.epherical.professions.networking.server.S2CCategorySyncPayload;
import com.epherical.professions.networking.server.S2CExperienceGainPayload;
import com.epherical.professions.networking.server.S2CPlayerDataSyncPayload;
import com.epherical.professions.presentation.commands.ProfessionsStandardCommands;
import com.epherical.professions.registries.ActionLoad3;
import com.epherical.professions.registries.CategoryLoad3;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.fabric.api.event.registry.DynamicRegistries;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.fabricmc.fabric.api.event.registry.RegistryAttribute;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.LevelResource;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.nio.file.Path;
import java.util.Optional;

public class FabricProfessionsMod extends ProfessionsCommon implements ModInitializer {

    private static final FabricRegistrarBackend FABRIC_REGISTRAR_BACKEND = new FabricRegistrarBackend();

    public static Registry<ActionType> ACTIONS;
    public static Registry<ConditionType> CONDITIONS;
    public static Registry<RewardType> REWARDS;
    public static RegistryAccess REGISTRY_ACCESS;

    public static FabricProfessionsMod mod;

    private final ActionManager actionManager;
    private final PlayerManager playerManager;

    public FabricProfessionsMod() {
        super();
        this.actionManager = new ActionManager(null);
        this.playerManager = new PlayerManager(actionManager, null, getEventBus(), getCategoryManager());
    }

    @Override
    public void onInitialize() {
        mod = this;
        PlatformBootstrap.init(FABRIC_REGISTRAR_BACKEND);

        ACTIONS = FabricRegistryBuilder.createSimple(ACTION_REGISTRY_KEY)
                .attribute(RegistryAttribute.SYNCED)
                .buildAndRegister();
        CONDITIONS = FabricRegistryBuilder.createSimple(CONDITION_REGISTRY_KEY)
                .attribute(RegistryAttribute.SYNCED)
                .buildAndRegister();
        REWARDS = FabricRegistryBuilder.createSimple(REWARD_REGISTRY_KEY)
                .attribute(RegistryAttribute.SYNCED)
                .buildAndRegister();
        DynamicRegistries.registerSynced(PROFESSION_REGISTRY_KEY, Profession.CODEC);

        ProfessionsCommon.register();
        NetworkPayloadDispatcher.setPayloadSender(ServerPlayNetworking::send);

        registerNetworking();
        registerReloadListeners();
        registerLifecycleEvents();
        registerGameplayEvents();
        registerCommands();
    }

    private void registerNetworking() {
        PayloadTypeRegistry.playS2C().register(S2CExperienceGainPayload.TYPE, S2CExperienceGainPayload.STREAM_CODEC);
        PayloadTypeRegistry.playS2C().register(S2CCategorySyncPayload.TYPE, S2CCategorySyncPayload.STREAM_CODEC);
        PayloadTypeRegistry.playS2C().register(S2CPlayerDataSyncPayload.TYPE, S2CPlayerDataSyncPayload.STREAM_CODEC);
        PayloadTypeRegistry.playC2S().register(C2SCategorySelectionPayload.TYPE, C2SCategorySelectionPayload.STREAM_CODEC);

        ServerPlayNetworking.registerGlobalReceiver(C2SCategorySelectionPayload.TYPE,
                (payload, context) -> CategorySelectionPayloadHandler.handle(context.player(), payload));
    }

    private void registerReloadListeners() {
        ResourceManagerHelper.get(net.minecraft.server.packs.PackType.SERVER_DATA).registerReloadListener(FabricActionReloadListener.ID, provider -> {
            ActionLoad3 loader = new ActionLoad3(actionManager);
            setActionLoader(loader);
            return new FabricActionReloadListener(loader, provider);
        });
        ResourceManagerHelper.get(net.minecraft.server.packs.PackType.SERVER_DATA).registerReloadListener(FabricCategoryReloadListener.ID, provider -> {
            CategoryLoad3 loader = new CategoryLoad3(getCategoryManager());
            setCategoryLoader(loader);
            return new FabricCategoryReloadListener(loader, provider);
        });
    }

    private void registerLifecycleEvents() {
        ServerLifecycleEvents.SERVER_STARTING.register(server -> {
            REGISTRY_ACCESS = server.registryAccess();
            Path resolve = server.getWorldPath(LevelResource.ROOT).resolve("professions/playerdata");
            playerManager.setOccupationDataLoader(new UuidOccupationDataLoader(resolve, () -> REGISTRY_ACCESS));
            playerManager.setServer(server);
            playerManager.startExecutor();
        });
        ServerLifecycleEvents.SERVER_STARTED.register(server -> playerManager.loadAll());
        ServerLifecycleEvents.SERVER_STOPPING.register(server -> playerManager.shutdown());
        ServerLifecycleEvents.SERVER_STOPPED.register(server -> REGISTRY_ACCESS = null);
        ServerLifecycleEvents.END_DATA_PACK_RELOAD.register((server, resourceManager, success) -> {
            if (success) {
                REGISTRY_ACCESS = server.registryAccess();
            }
        });
        ServerLifecycleEvents.SYNC_DATA_PACK_CONTENTS.register((player, joined) -> syncPlayer(player));
    }

    private void registerGameplayEvents() {
        ServerPlayerEvents.JOIN.register(playerManager::playerJoined);
        ServerPlayerEvents.LEAVE.register(playerManager::playerQuit);

        PlayerBlockBreakEvents.AFTER.register((world, player, pos, state, blockEntity) -> {
            if (!(world instanceof ServerLevel serverLevel) || !(player instanceof ServerPlayer serverPlayer) || serverPlayer.isCreative()) {
                return;
            }

            IProfessionalPlayer professionalPlayer = ensureProfessionalPlayer(serverPlayer);
            if (professionalPlayer == null) {
                return;
            }

            Holder<Block> blockHolder = state.getBlockHolder();
            ProfessionContext.Builder builder = ProfessionContext.builder(serverLevel, Actions.BLOCK_BREAK, professionalPlayer)
                    .addParameter(ProfessionParameter.THIS_BLOCK, state)
                    .addParameter(ProfessionParameter.BLOCKPOS, pos)
                    .addParameter(ProfessionParameter.TOOL, serverPlayer.getMainHandItem())
                    .addParameter(ProfessionParameter.ITEM_INVOLVED, serverPlayer.getWeaponItem())
                    .addParameter(ProfessionParameter.THIS_HOLDER, blockHolder);
            playerManager.processAction(serverPlayer, builder.build());
        });

        ServerLivingEntityEvents.AFTER_DEATH.register(this::onEntityDeath);
    }

    private void registerCommands() {
        CommandRegistrationCallback.EVENT.register(
                (dispatcher, buildContext, environment) ->
                        new ProfessionsStandardCommands(this, dispatcher, buildContext, actionManager)
        );
    }

    private void onEntityDeath(LivingEntity entity, net.minecraft.world.damagesource.DamageSource damageSource) {
        if (!(entity.level() instanceof ServerLevel serverLevel)) {
            return;
        }

        Entity source = damageSource.getEntity();
        if (!(source instanceof ServerPlayer serverPlayer)) {
            return;
        }

        if (entity instanceof AgeableMob ageableMob && ageableMob.isBaby()) {
            return;
        }

        IProfessionalPlayer professionalPlayer = ensureProfessionalPlayer(serverPlayer);
        if (professionalPlayer == null) {
            return;
        }

        ProfessionContext.Builder builder = ProfessionContext.builder(serverLevel, Actions.SLAY_ACTION, professionalPlayer)
                .addParameter(ProfessionParameter.THIS_PLAYER, professionalPlayer)
                .addParameter(ProfessionParameter.ENTITY, entity);
        playerManager.processAction(serverPlayer, builder.build());
    }

    private void syncPlayer(ServerPlayer player) {
        IProfessionalPlayer professionalPlayer = ensureProfessionalPlayer(player);
        if (professionalPlayer == null) {
            return;
        }

        NetworkPayloadDispatcher.sendToPlayer(player, new S2CCategorySyncPayload(getCategoryManager().getCategoryMap()));
        S2CPlayerDataSyncPayload payload = new S2CPlayerDataSyncPayload(
                player.getUUID(),
                professionalPlayer.getAllOccupations(),
                Optional.ofNullable(playerManager.getCategoryIdFor(professionalPlayer)),
                playerManager.getRelevantActionsForCategory(professionalPlayer.getCategory())
        );
        NetworkPayloadDispatcher.sendToPlayer(player, payload);
    }

    private @Nullable IProfessionalPlayer ensureProfessionalPlayer(ServerPlayer player) {
        IProfessionalPlayer professionalPlayer = playerManager.getPlayer(player.getUUID());
        if (professionalPlayer == null) {
            playerManager.playerJoined(player);
            professionalPlayer = playerManager.getPlayer(player.getUUID());
        }
        return professionalPlayer;
    }

    @Override
    public PlayerManager getPlayerManager() {
        return playerManager;
    }

    @Override
    public File getModDir() {
        return FabricLoader.getInstance().getConfigDir().toFile();
    }

    @Override
    public ActionManager getActionManager() {
        return actionManager;
    }

    public static @Nullable IProfessionalPlayer ensureProfessionalPlayer(FabricProfessionsMod mod, ServerPlayer player) {
        IProfessionalPlayer professionalPlayer = mod.getPlayerManager().getPlayer(player.getUUID());
        if (professionalPlayer == null) {
            mod.getPlayerManager().playerJoined(player);
            professionalPlayer = mod.getPlayerManager().getPlayer(player.getUUID());
        }
        return professionalPlayer;
    }
}

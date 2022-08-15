package com.epherical.professions.triggers;

import com.epherical.professions.CommonPlatform;
import com.epherical.professions.ProfessionsForge;
import com.epherical.professions.api.ProfessionalPlayer;
import com.epherical.professions.capability.impl.ChunkVisitedImpl;
import com.epherical.professions.capability.impl.PlayerOwnableImpl;
import com.epherical.professions.profession.ProfessionContext;
import com.epherical.professions.profession.ProfessionParameter;
import com.epherical.professions.profession.action.Actions;
import com.epherical.professions.util.ProfessionUtil;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.levelgen.feature.ConfiguredStructureFeature;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class ProfessionListener {

    private Map<UUID, ChunkPos> playerPositions = new HashMap<>();

    @SubscribeEvent()
    public void onBlockEntity(BlockEvent.EntityPlaceEvent event) {
        if (event.isCanceled() || !(event.getEntity() instanceof ServerPlayer)) {
            return;
        }
        BlockEntity entity = event.getWorld().getBlockEntity(event.getPos());
        if (entity != null) {
            entity.getCapability(PlayerOwnableImpl.OWNING_CAPABILITY).ifPresent(ownable -> {
                ownable.setPlacedBy((ServerPlayer) event.getEntity());
            });
        }
    }

    @SubscribeEvent
    public void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        if (!event.getPlayer().getLevel().isClientSide) {
            ProfessionsForge.getInstance().getPlayerManager().playerJoined((ServerPlayer) event.getPlayer());
        }

    }

    @SubscribeEvent
    public void onPlayerLeave(PlayerEvent.PlayerLoggedOutEvent event) {
        if (!event.getPlayer().getLevel().isClientSide) {
            ProfessionsForge.getInstance().getPlayerManager().playerQuit((ServerPlayer) event.getPlayer());
            playerPositions.remove(event.getPlayer().getUUID());
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGH, receiveCanceled = false)
    public void onBlockBreak(BlockEvent.BreakEvent event) {
        if (event.isCanceled() || event.getWorld().isClientSide()) {
            return;
        }

        ProfessionalPlayer player = CommonPlatform.platform.getPlayerManager().getPlayer(event.getPlayer().getUUID());
        if (player == null) {
            return;
        }

        // want the opposite, if it returns true, we want to set it to false, so it doesn't actually cancel the event.
        event.setCanceled(!ProfessionUtil.canBreak(player, event.getPlayer(), event.getState().getBlock()));
    }

    @SubscribeEvent()
    public void onEntityUpdate(LivingEvent.LivingUpdateEvent event) {
        LivingEntity entity = event.getEntityLiving();
        if (event.isCanceled() || entity.level.isClientSide || !(entity instanceof ServerPlayer)) {
            return;
        }
        ServerPlayer player = (ServerPlayer) event.getEntityLiving();
        UUID uuid = player.getUUID();
        if (!playerPositions.containsKey(uuid)) {
            playerPositions.put(uuid, player.chunkPosition());
        } else {
            ChunkPos pos = playerPositions.get(uuid);
            // shifted chunks
            if (!pos.equals(player.chunkPosition())) {
                ChunkPos newPos = player.chunkPosition();
                LevelChunk access = player.getLevel().getChunk(newPos.x, newPos.z);
                access.getCapability(ChunkVisitedImpl.CHUNK_VISITED_CAPABILITY).ifPresent(chunkVisited -> {
                    if (!chunkVisited.hasPlayerExploredChunk(uuid)) {
                        Holder<Biome> biomeHolder = player.getLevel().getBiome(player.getOnPos());
                        ProfessionContext.Builder builder = new ProfessionContext.Builder(player.getLevel())
                                .addRandom(player.getLevel().random)
                                .addParameter(ProfessionParameter.ACTION_TYPE, Actions.EXPLORE_BIOME)
                                .addParameter(ProfessionParameter.THIS_PLAYER, ProfessionsForge.getInstance().getPlayerManager().getPlayer(player.getUUID()))
                                .addParameter(ProfessionParameter.BIOME, biomeHolder);

                        playerPositions.put(uuid, player.chunkPosition());
                        if (RewardHandler.handleReward(builder)) {
                            chunkVisited.addPlayerToChunk(uuid);
                            access.setUnsaved(true);
                        }
                        Set<ConfiguredStructureFeature<?, ?>> configuredStructureFeatures = player.getLevel().structureFeatureManager().getAllStructuresAt(player.getOnPos()).keySet();
                        for (ConfiguredStructureFeature<?, ?> configuredStructureFeature : configuredStructureFeatures) {
                            builder = new ProfessionContext.Builder(player.getLevel())
                                    .addRandom(player.getLevel().random)
                                    .addParameter(ProfessionParameter.ACTION_TYPE, Actions.EXPLORE_STRUCT)
                                    .addParameter(ProfessionParameter.THIS_PLAYER, ProfessionsForge.getInstance().getPlayerManager().getPlayer(player.getUUID()))
                                    .addParameter(ProfessionParameter.CONFIGURED_STRUCTURE, configuredStructureFeature);
                            RewardHandler.handleReward(builder);
                        }
                    } else {
                        playerPositions.put(uuid, player.chunkPosition());
                    }
                });
            }
        }
    }
}

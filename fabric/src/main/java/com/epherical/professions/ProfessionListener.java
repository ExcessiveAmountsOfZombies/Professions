package com.epherical.professions;

import com.epherical.professions.integration.cardinal.ChunkComponent;
import com.epherical.professions.integration.cardinal.ChunksExploredComponent;
import com.epherical.professions.profession.ProfessionContext;
import com.epherical.professions.profession.ProfessionParameter;
import com.epherical.professions.profession.action.Actions;
import com.epherical.professions.trigger.RewardHandler;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.core.Holder;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.levelgen.structure.Structure;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class ProfessionListener {

    private Map<UUID, ChunkPos> playerPositions = new HashMap<>();

    public void onPlayerJoin(ServerGamePacketListenerImpl handler, PacketSender sender, MinecraftServer server) {
        try {
            ProfessionsFabric.getInstance().getPlayerManager().playerJoined(handler.getPlayer());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void onPlayerLeave(ServerGamePacketListenerImpl handler, MinecraftServer server) {
        try {
            ProfessionsFabric.getInstance().getPlayerManager().playerQuit(handler.getPlayer());
            playerPositions.remove(handler.getPlayer().getUUID());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onPlayerTick(ServerPlayer player, ProfessionsFabric fabric) {
        UUID uuid = player.getUUID();
        if (!playerPositions.containsKey(uuid)) {
            playerPositions.put(uuid, player.chunkPosition());
        } else {
            ChunkPos pos = playerPositions.get(uuid);
            // shifted chunks
            if (!pos.equals(player.chunkPosition())) {
                ChunkPos newPos = player.chunkPosition();
                LevelChunk access = player.serverLevel().getChunk(newPos.x, newPos.z);
                ChunksExploredComponent component = access.getComponent(ChunkComponent.CHUNKS_EXPLORED);
                if (!component.hasPlayerExploredChunk(uuid)) {
                    Holder<Biome> biomeHolder = player.serverLevel().getBiome(player.getOnPos());
                    ProfessionContext.Builder builder = new ProfessionContext.Builder(player.serverLevel())
                            .addRandom(player.serverLevel().getRandom())
                            .addParameter(ProfessionParameter.ACTION_TYPE, Actions.EXPLORE_BIOME)
                            .addParameter(ProfessionParameter.THIS_PLAYER, fabric.getPlayerManager().getPlayer(player.getUUID()))
                            .addParameter(ProfessionParameter.BIOME, biomeHolder);

                    playerPositions.put(uuid, player.chunkPosition());
                    if (RewardHandler.handleReward(builder)) {
                        component.addPlayerToChunk(uuid);
                        access.setUnsaved(true);
                    }
                    Set<Structure> configuredStructureFeatures = player.serverLevel().structureManager().getAllStructuresAt(player.getOnPos()).keySet();
                    for (Structure configuredStructureFeature : configuredStructureFeatures) {
                        if (player.serverLevel().structureManager().getStructureAt(player.getOnPos(), configuredStructureFeature).isValid()) {
                            builder = new ProfessionContext.Builder(player.serverLevel())
                                    .addRandom(player.serverLevel().getRandom())
                                    .addParameter(ProfessionParameter.ACTION_TYPE, Actions.EXPLORE_STRUCT)
                                    .addParameter(ProfessionParameter.THIS_PLAYER, fabric.getPlayerManager().getPlayer(player.getUUID()))
                                    .addParameter(ProfessionParameter.CONFIGURED_STRUCTURE, configuredStructureFeature);
                            RewardHandler.handleReward(builder);
                        }
                    }
                } else {
                    playerPositions.put(uuid, player.chunkPosition());
                }
            }
        }

    }
}

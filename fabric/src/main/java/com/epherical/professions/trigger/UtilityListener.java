package com.epherical.professions.trigger;

import com.epherical.professions.ProfessionsFabric;
import com.epherical.professions.events.SyncEvents;
import com.epherical.professions.events.trigger.TriggerEvents;
import com.epherical.professions.util.mixins.PlayerOwnable;
import com.google.common.collect.Maps;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.commands.Commands;
import net.minecraft.network.protocol.game.ClientboundBlockBreakAckPacket;
import net.minecraft.network.protocol.game.ClientboundRemoveMobEffectPacket;
import net.minecraft.network.protocol.game.ClientboundUpdateMobEffectPacket;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket;
import net.minecraft.server.TickTask;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static net.minecraft.network.protocol.game.ServerboundPlayerActionPacket.Action.*;

public class UtilityListener {

    private static Map<UUID, MobEffectInstance> EFFECTS = Maps.newHashMap();
    private static Map<Integer, Runnable> RUNN = new HashMap<>();

    public static void init(ProfessionsFabric mod) {
        TriggerEvents.PLACE_BLOCK_EVENT.register((player, state, pos) -> {
            ServerLevel level = player.getLevel().getLevel();
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof PlayerOwnable owned) {
                owned.professions$setPlacedBy(player);
            }
        });

        AttackBlockCallback.EVENT.register((player, world, hand, pos, direction) -> {
            if (world.isClientSide || player.isSpectator()) {
                return InteractionResult.PASS;
            }
            if (mod.getPlayerManager().isSynchronized(player.getUUID())) {
                return InteractionResult.PASS; // only care about vanilla clients here.
            }
            MobEffectInstance fatigue = new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 2000, 4, false, false, false);
            ServerPlayer serverPlayer = (ServerPlayer) player;
            EFFECTS.put(serverPlayer.getUUID(), fatigue);
            serverPlayer.connection.send(new ClientboundUpdateMobEffectPacket(player.getId(), fatigue));
            BlockState state = world.getBlockState(pos);
            System.out.println("rip bozoing");

            return InteractionResult.PASS;
        });

        SyncEvents.ABORTED_BLOCK_DESTROY_EVENT.register((pos, player, state, level, action) -> {
            if (mod.getPlayerManager().isSynchronized(player.getUUID())) {
                return; // only care about vanilla clients here.
            }
            // TODO: if the player is destroying a block, we receive a START_DESTROYING_BLOCK
            //  if the player destroys the block, but it's not ready(? doesn't matter) we receive a STOP_DESTROY_BLOCK
            //  if the player stops before the block is destroyed, we can just remove the potion effect
            //  if they continue to destroy the block, we will receive more STOP_DESTROY_BLOCK notifications
            //  so we can store the fact that the player had a START_DESTROYING_BLOCK, and if we continue receiving STOP_DESTROY_BLOCKs
            //  without corresponding starts, we can give the player mining fatigue again until we receive an ABORT_DESTROY_BLOCK.
            //  so
            //  On STARTDB -> player goes into map
            //  on ABORTDB -> remove player from map and remove potion effect
            //  on STOPDB  -> wait 5 ticks, remove the fatigue, and listen to see if we receive more STOPDBs, if we do, and have no STARTDB, give fatigue
            //  .
            //  ALSO need to find a way to disable the hasDelayedDestroy

            if (action == ABORT_DESTROY_BLOCK) {
                MobEffectInstance effect = EFFECTS.get(player.getUUID());
                if (effect != null) {
                    player.connection.send(new ClientboundRemoveMobEffectPacket(player.getId(), EFFECTS.get(player.getUUID()).getEffect()));
                    System.out.println("removing");
                    EFFECTS.remove(player.getUUID());
                }
            } else if (action == STOP_DESTROY_BLOCK) {
                RUNN.put(level.getServer().getTickCount() + 5, () -> {
                    MobEffectInstance effect = EFFECTS.get(player.getUUID());
                    if (effect != null) {
                        player.connection.send(new ClientboundRemoveMobEffectPacket(player.getId(), EFFECTS.get(player.getUUID()).getEffect()));
                        System.out.println("removing");
                        EFFECTS.remove(player.getUUID());
                    }
                });
            }
        });

        ServerTickEvents.END_SERVER_TICK.register(server -> {
            RUNN.getOrDefault(server.getTickCount(), () -> {

            }).run();
        });

    }
}

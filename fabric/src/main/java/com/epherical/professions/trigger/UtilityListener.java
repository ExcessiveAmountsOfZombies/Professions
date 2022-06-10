package com.epherical.professions.trigger;

import com.epherical.professions.ProfessionsFabric;
import com.epherical.professions.events.SyncEvents;
import com.epherical.professions.events.trigger.TriggerEvents;
import com.epherical.professions.util.mixins.PlayerOwnable;
import com.google.common.collect.Maps;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.minecraft.network.protocol.game.ClientboundRemoveMobEffectPacket;
import net.minecraft.network.protocol.game.ClientboundUpdateMobEffectPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static net.minecraft.network.protocol.game.ServerboundPlayerActionPacket.Action.*;

public class UtilityListener {

    private static Map<UUID, MobEffectInstance> EFFECTS = Maps.newHashMap();
    private static Map<Integer, Runnable> runnables = new HashMap<>();
    private static Set<UUID> playersDestroying = new HashSet<>();

    public static void init(ProfessionsFabric mod) {
        TriggerEvents.PLACE_BLOCK_EVENT.register((player, state, pos) -> {
            ServerLevel level = player.getLevel().getLevel();
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof PlayerOwnable owned) {
                owned.professions$setPlacedBy(player);
            }
        });
        // TODO; should the inventory also be listened for? check their item in and and give them more fatigue?

        AttackBlockCallback.EVENT.register((player, world, hand, pos, direction) -> {
            if (world.isClientSide || player.isSpectator()) {
                return InteractionResult.PASS;
            }
            if (mod.getPlayerManager().isSynchronized(player.getUUID())) {
                return InteractionResult.PASS; // only care about vanilla clients here.
            }
            addMobEffect(player);
            playersDestroying.add(player.getUUID());

            return InteractionResult.PASS;
        });

        SyncEvents.ABORTED_BLOCK_DESTROY_EVENT.register((pos, player, state, level, action) -> {
            if (mod.getPlayerManager().isSynchronized(player.getUUID())) {
                return; // only care about vanilla clients here.
            }
            if (action == ABORT_DESTROY_BLOCK) {
                removeMobEffect(player);
                updateMobEffect(player);
            } else if (action == STOP_DESTROY_BLOCK) {
                if (playersDestroying.contains(player.getUUID())) {
                    runnables.put(level.getServer().getTickCount() + 5, () -> {
                        removeMobEffect(player);
                        updateMobEffect(player);
                    });
                    playersDestroying.remove(player.getUUID());
                } else {
                    addMobEffect(player);
                }
            }
            //  ALSO need to find a way to disable the hasDelayedDestroy
        });

        ServerTickEvents.END_SERVER_TICK.register(server -> {
            runnables.getOrDefault(server.getTickCount(), () -> {}).run();
            runnables.remove(server.getTickCount());
        });
    }

    private static void removeMobEffect(ServerPlayer player) {
        MobEffectInstance effect = EFFECTS.get(player.getUUID());
        if (effect != null) {
            player.connection.send(new ClientboundRemoveMobEffectPacket(player.getId(), EFFECTS.get(player.getUUID()).getEffect()));
            EFFECTS.remove(player.getUUID());
        }
    }

    private static void addMobEffect(Player player) {
        MobEffectInstance fatigue = new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 2000, 4, false, false, false);
        ServerPlayer serverPlayer = (ServerPlayer) player;
        EFFECTS.put(serverPlayer.getUUID(), fatigue);
        serverPlayer.connection.send(new ClientboundUpdateMobEffectPacket(player.getId(), fatigue));
    }

    private static void updateMobEffect(ServerPlayer player) {
        MobEffectInstance instance = player.getEffect(MobEffects.DIG_SLOWDOWN);
        if (instance != null) {
            player.connection.send(new ClientboundUpdateMobEffectPacket(player.getId(), instance));
        }
    }
}

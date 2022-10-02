package com.epherical.professions.trigger;

import com.epherical.professions.CommonPlatform;
import com.epherical.professions.ProfessionsFabric;
import com.epherical.professions.api.ProfessionalPlayer;
import com.epherical.professions.events.SyncEvents;
import com.epherical.professions.events.trigger.TriggerEvents;
import com.epherical.professions.profession.progression.Occupation;
import com.epherical.professions.profession.unlock.Unlock;
import com.epherical.professions.profession.unlock.Unlocks;
import com.epherical.professions.util.ProfessionUtil;
import com.google.common.collect.Maps;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.network.protocol.game.ClientboundRemoveMobEffectPacket;
import net.minecraft.network.protocol.game.ClientboundUpdateMobEffectPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static net.minecraft.network.protocol.game.ServerboundPlayerActionPacket.Action.ABORT_DESTROY_BLOCK;
import static net.minecraft.network.protocol.game.ServerboundPlayerActionPacket.Action.STOP_DESTROY_BLOCK;

public class UtilityListener {

    private static Map<UUID, MobEffectInstance> EFFECTS = Maps.newHashMap();
    private static Map<Integer, Runnable> runnables = new HashMap<>();
    private static Set<UUID> playersDestroying = new HashSet<>();
    // for cancelling hasDelayedDestroy in ServerPlayerGameModeMixin.
    public static Set<UUID> playerDestroyedWithLockedItem = new HashSet<>();

    public static void init(ProfessionsFabric mod) {
        TriggerEvents.PLACE_BLOCK_EVENT.register((player, state, pos) -> {
            ServerLevel level = player.getLevel().getLevel();
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof PlayerOwnable owned) {
                owned.professions$setPlacedBy(player);
            }
        });

        ServerPlayerEvents.COPY_FROM.register((oldPlayer, newPlayer, alive) -> {
            ProfessionalPlayer pPlayer = CommonPlatform.platform.getPlayerManager().getPlayer(oldPlayer.getUUID());
            if (pPlayer != null) {
                pPlayer.setPlayer(newPlayer);
                for (Occupation activeOccupation : pPlayer.getActiveOccupations()) {
                    activeOccupation.getProfession().getBenefits().handleLevelUp(pPlayer, activeOccupation);
                }
            }
        });

        TriggerEvents.PLAYER_USE_ITEM_ON_EVENT.register((player, level, stack, hand, hitResult) -> {
            if (player.isSpectator()) {
                return InteractionResult.PASS;
            }

            ProfessionalPlayer pPlayer = ProfessionsFabric.getInstance().getPlayerManager().getPlayer(player.getUUID());
            if (pPlayer == null) {
                return InteractionResult.PASS;
            }

            if (stack.getItem().equals(Items.AIR)) {
                return InteractionResult.PASS;
            }

            List<Unlock.Singular<Item>> lockedKnowledge = pPlayer.getLockedKnowledge(stack.getItem(), Set.of(Unlocks.INTERACTION_UNLOCK));
            for (Unlock.Singular<Item> singular : lockedKnowledge) {
                if (!singular.canUse(pPlayer)) {

                    return InteractionResult.FAIL;
                }
            }

            return InteractionResult.PASS;
        });


        PlayerBlockBreakEvents.BEFORE.register((world, player, pos, state, blockEntity) -> {
            if (world.isClientSide) {
                return true;
            }

            ProfessionalPlayer pPlayer = CommonPlatform.platform.getPlayerManager().getPlayer(player.getUUID());
            if (pPlayer == null) {
                return true;
            }

            return ProfessionUtil.canBreak(pPlayer, player, state.getBlock());
        });

        AttackBlockCallback.EVENT.register((player, world, hand, pos, direction) -> {
            if (world.isClientSide || player.isSpectator()) {
                return InteractionResult.PASS;
            }
            if (mod.getPlayerManager().isSynchronized(player.getUUID())) {
                return InteractionResult.PASS; // only care about vanilla clients here.
            }
            ProfessionalPlayer professionalPlayer = ProfessionsFabric.getInstance().getPlayerManager().getPlayer(player.getUUID());
            if (professionalPlayer == null) {
                return InteractionResult.PASS;
            }
            ItemStack item = player.getItemInHand(hand);
            boolean pair = ProfessionUtil.canUse(professionalPlayer, Unlocks.TOOL_UNLOCK, item.getItem());
            if (!pair) {
                addMobEffect(player);
                playersDestroying.add(player.getUUID());
            }
            return InteractionResult.PASS;
        });

        SyncEvents.ABORTED_BLOCK_DESTROY_EVENT.register((pos, player, state, level, action) -> {
            if (mod.getPlayerManager().isSynchronized(player.getUUID())) {
                return; // only care about vanilla clients here.
            }
            if (action == ABORT_DESTROY_BLOCK && playersDestroying.contains(player.getUUID())) {
                removeMobEffect(player);
                updateMobEffect(player);
            } else if (action == STOP_DESTROY_BLOCK) {
                // this will activate the "hasDelayedDestroy" in ServerPlayerGameMode. that needs to be set back to false
                // so that the block doesn't break when the player swaps slots.
                if (playersDestroying.contains(player.getUUID())) {
                    runnables.put(level.getServer().getTickCount() + 5, () -> {
                        // in 5 ticks we will remove the mining fatigue and update the fatigue to resynchronize the server determined fatigue, if it exists.
                        removeMobEffect(player);
                        updateMobEffect(player);
                    });
                    if (!checkIfUnlockisGood(player)) {
                        playerDestroyedWithLockedItem.add(player.getUUID());
                    }
                    playersDestroying.remove(player.getUUID());
                } else {
                    // now that the player has no fatigue, and they're STILL activating STOP_DB, we add the mob effect, and also cancel
                    // hasDelayedDestroy by adding the player to playerDestroyedWithLockedItem.
                    if (!checkIfUnlockisGood(player)) {
                        addMobEffect(player);
                        playerDestroyedWithLockedItem.add(player.getUUID());
                        playersDestroying.remove(player.getUUID());
                    }
                }
            }
        });

        ServerTickEvents.END_SERVER_TICK.register(server -> {
            runnables.getOrDefault(server.getTickCount(), () -> {
            }).run();
            runnables.remove(server.getTickCount());
        });

        /*PermissionCheckEvent.EVENT.register(PermissionCheckEvent.CONTENT_PHASE, (sharedSuggestionProvider, s) -> {
            if (sharedSuggestionProvider instanceof CommandSourceStack stack) {
                try {

                    ServerPlayer player = stack.getPlayerOrException();
                    ProfessionalPlayer pPlayer = ProfessionsFabric.getInstance().getPlayerManager().getPlayer(player.getUUID());
                    if (pPlayer != null) {
                        for (Perk perkType : pPlayer.getPerkByType(Perks.PERMISSION_PERK_TYPE)) {
                        }
                    }

                    // todo: permissions are called often, we will want a filter, or maybe even some async code here,
                    //  or even some cache
                    if (pPlayer != null && pPlayer.canUsePerk(s, pPlayer)) {
                        return TriState.TRUE;
                    }

                } catch (CommandSyntaxException e) {
                    return TriState.DEFAULT;
                }
            } else {
                return TriState.DEFAULT;
            }
            return TriState.DEFAULT;
        });*/
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

    private static boolean checkIfUnlockisGood(ServerPlayer player) {
        ProfessionalPlayer professionalPlayer = ProfessionsFabric.getInstance().getPlayerManager().getPlayer(player.getUUID());
        if (professionalPlayer == null) {
            return true;
        }
        ItemStack item = player.getMainHandItem();
        return ProfessionUtil.canUse(professionalPlayer, Unlocks.TOOL_UNLOCK, item.getItem());
    }
}

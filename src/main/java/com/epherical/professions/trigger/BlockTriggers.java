package com.epherical.professions.trigger;

import com.epherical.professions.ProfessionsMod;
import com.epherical.professions.config.ProfessionConfig;
import com.epherical.professions.events.trigger.TriggerEvents;
import com.epherical.professions.profession.ProfessionContext;
import com.epherical.professions.profession.ProfessionParameter;
import com.epherical.professions.profession.action.Actions;
import com.epherical.professions.util.EnchantmentContainer;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalField;
import java.util.Map;

public class BlockTriggers {

    private static final Cache<BlockPos, Instant> cache = CacheBuilder
            .newBuilder()
            .expireAfterWrite(Duration.ofSeconds(30))
            .maximumSize(5000)
            .build();


    public static void init(ProfessionsMod mod) {
        PlayerBlockBreakEvents.AFTER.register((world, player, pos, state, blockEntity) -> {
            // only ever run on the server.
            if (world instanceof ServerLevel) {
                Instant placementTime = cache.getIfPresent(pos);
                ProfessionContext.Builder builder = new ProfessionContext.Builder((ServerLevel) world)
                        .addRandom(world.random)
                        .addParameter(ProfessionParameter.ACTION_TYPE, Actions.BREAK_BLOCK)
                        .addParameter(ProfessionParameter.BLOCKPOS, pos)
                        .addParameter(ProfessionParameter.THIS_PLAYER, mod.getPlayerManager().getPlayer(player.getUUID()))
                        .addParameter(ProfessionParameter.THIS_BLOCKSTATE, state)
                        .addParameter(ProfessionParameter.TOOL, player.getMainHandItem());
                if (placementTime != null) {
                    Instant now = Instant.now();
                    if (now.isAfter(placementTime)) {
                        cache.cleanUp();
                        RewardHandler.handleReward(builder);
                    } else {
                        long seconds = Duration.between(now, placementTime).get(ChronoUnit.SECONDS);
                        sendCooldownMessage((ServerPlayer) player, seconds);
                    }
                } else {
                    RewardHandler.handleReward(builder);
                }
            }
        });

        TriggerEvents.PLACE_BLOCK_EVENT.register((player, state, pos) -> {
            Instant placementTime = cache.getIfPresent(pos);
            ServerLevel level = player.getLevel();
            ProfessionContext.Builder builder = new ProfessionContext.Builder(level)
                    .addRandom(level.random)
                    .addParameter(ProfessionParameter.ACTION_TYPE, Actions.PLACE_BLOCK)
                    .addParameter(ProfessionParameter.BLOCKPOS, pos)
                    .addParameter(ProfessionParameter.THIS_BLOCKSTATE, state)
                    .addParameter(ProfessionParameter.THIS_PLAYER, mod.getPlayerManager().getPlayer(player.getUUID()));
            if (placementTime != null) {
                Instant now = Instant.now();
                if (now.isAfter(placementTime)) {
                    cache.cleanUp();
                    cache.put(pos, now.plus(ProfessionConfig.paymentCoolDown, ChronoUnit.SECONDS));
                    RewardHandler.handleReward(builder);
                } else {
                    long seconds = Duration.between(now, placementTime).get(ChronoUnit.SECONDS);
                    sendCooldownMessage(player, seconds);
                }
            } else {
                cache.put(pos, Instant.now().plus(ProfessionConfig.paymentCoolDown, ChronoUnit.SECONDS));
                RewardHandler.handleReward(builder);
            }
        });

        TriggerEvents.TNT_DESTROY_EVENT.register((source, state) -> {
            ServerLevel level = source.getLevel();
            ProfessionContext.Builder builder = new ProfessionContext.Builder(level)
                    .addRandom(level.random)
                    .addParameter(ProfessionParameter.ACTION_TYPE, Actions.TNT_DESTROY)
                    .addParameter(ProfessionParameter.THIS_BLOCKSTATE, state)
                    .addParameter(ProfessionParameter.THIS_PLAYER, mod.getPlayerManager().getPlayer(source.getUUID()));
            RewardHandler.handleReward(builder);
        });

        TriggerEvents.SMELT_ITEM_EVENT.register((owner, smeltedItem, recipe, blockEntity) -> {
            if (blockEntity.getLevel() != null && !blockEntity.getLevel().isClientSide) {
                ServerLevel level = (ServerLevel) blockEntity.getLevel();
                ProfessionContext.Builder builder = new ProfessionContext.Builder(level)
                        .addRandom(level.random)
                        .addParameter(ProfessionParameter.THIS_PLAYER, mod.getPlayerManager().getPlayer(owner))
                        .addParameter(ProfessionParameter.ACTION_TYPE, Actions.ON_ITEM_COOK)
                        .addParameter(ProfessionParameter.ITEM_INVOLVED, smeltedItem)
                        .addParameter(ProfessionParameter.THIS_BLOCKSTATE, blockEntity.getBlockState())
                        .addParameter(ProfessionParameter.RECIPE_CRAFTED, recipe);
                RewardHandler.handleReward(builder);
            }
        });

        TriggerEvents.BREW_POTION_EVENT.register((owner, brewingIngredient, blockEntity) -> {
            if (blockEntity.getLevel() != null && !blockEntity.getLevel().isClientSide) {
                ServerLevel level = (ServerLevel) blockEntity.getLevel();
                ProfessionContext.Builder builder = new ProfessionContext.Builder(level)
                        .addRandom(level.random)
                        .addParameter(ProfessionParameter.THIS_PLAYER, mod.getPlayerManager().getPlayer(owner))
                        .addParameter(ProfessionParameter.ACTION_TYPE, Actions.BREW_ITEM)
                        .addParameter(ProfessionParameter.ITEM_INVOLVED, brewingIngredient)
                        .addParameter(ProfessionParameter.THIS_BLOCKSTATE, blockEntity.getBlockState());
                RewardHandler.handleReward(builder);
            }
        });

        TriggerEvents.ENCHANT_ITEM_EVENT.register((player, itemEnchanted, spentLevels) -> {
            ServerLevel level = player.getLevel();
            ProfessionContext.Builder builder = new ProfessionContext.Builder(level)
                    .addRandom(level.random)
                    .addParameter(ProfessionParameter.THIS_PLAYER, mod.getPlayerManager().getPlayer(player.getUUID()));

            for (Map.Entry<Enchantment, Integer> entry : EnchantmentHelper.getEnchantments(itemEnchanted).entrySet()) {
                builder.addParameter(ProfessionParameter.ACTION_TYPE, Actions.ENCHANT_ITEM)
                        .addParameter(ProfessionParameter.ENCHANTMENT, new EnchantmentContainer(entry.getKey(), entry.getValue()));
                RewardHandler.handleReward(builder);
            }

            builder = new ProfessionContext.Builder(level)
                    .addRandom(level.random)
                    .addParameter(ProfessionParameter.THIS_PLAYER, mod.getPlayerManager().getPlayer(player.getUUID()))
                    .addParameter(ProfessionParameter.ACTION_TYPE, Actions.ENCHANT_ITEM)
                    .addParameter(ProfessionParameter.ITEM_INVOLVED, itemEnchanted);
            RewardHandler.handleReward(builder);

        });
    }

    private static void sendCooldownMessage(ServerPlayer player, long seconds) {
        if (seconds < 0) {
            seconds = 0;
        }
        // need bright colors in the action bar for whatever reason, otherwise would use variables/errors
        MutableComponent message = new TranslatableComponent("This position is on cooldown, wait %s more second(s) before trying again.",
                        new TextComponent(String.valueOf(seconds)).setStyle(Style.EMPTY.withColor(ProfessionConfig.descriptors)))
                        .setStyle(Style.EMPTY.withColor(ProfessionConfig.variables));
        player.sendMessage(message, ChatType.GAME_INFO, Util.NIL_UUID);
    }
}

package com.epherical.professions.trigger;

import com.epherical.professions.ProfessionsFabric;
import com.epherical.professions.events.trigger.TriggerEvents;
import com.epherical.professions.profession.ProfessionContext;
import com.epherical.professions.profession.ProfessionParameter;
import com.epherical.professions.profession.action.Actions;
import com.epherical.professions.util.EnchantmentContainer;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;

import java.util.Map;

public class BlockTriggers {

    public static void init(ProfessionsFabric mod) {
        PlayerBlockBreakEvents.AFTER.register((world, player, pos, state, blockEntity) -> {
            // only ever run on the server.
            if (world instanceof ServerLevel) {
                ProfessionContext.Builder builder = new ProfessionContext.Builder((ServerLevel) world)
                        .addRandom(world.random)
                        .addParameter(ProfessionParameter.ACTION_TYPE, Actions.BREAK_BLOCK)
                        .addParameter(ProfessionParameter.BLOCKPOS, pos)
                        .addParameter(ProfessionParameter.THIS_PLAYER, mod.getPlayerManager().getPlayer(player.getUUID()))
                        .addParameter(ProfessionParameter.THIS_BLOCKSTATE, state)
                        .addParameter(ProfessionParameter.TOOL, player.getMainHandItem());
                RewardHandler.handleReward(builder);
            }
        });

        TriggerEvents.PLACE_BLOCK_EVENT.register((player, state, pos) -> {
            ServerLevel level = player.getLevel();
            ProfessionContext.Builder builder = new ProfessionContext.Builder(level)
                    .addRandom(level.random)
                    .addParameter(ProfessionParameter.ACTION_TYPE, Actions.PLACE_BLOCK)
                    .addParameter(ProfessionParameter.BLOCKPOS, pos)
                    .addParameter(ProfessionParameter.THIS_BLOCKSTATE, state)
                    .addParameter(ProfessionParameter.THIS_PLAYER, mod.getPlayerManager().getPlayer(player.getUUID()));
            RewardHandler.handleReward(builder);
        });

        TriggerEvents.TNT_DESTROY_EVENT.register((source, state, blockPos) -> {
            ServerLevel level = source.getLevel();
            ProfessionContext.Builder builder = new ProfessionContext.Builder(level)
                    .addRandom(level.random)
                    .addParameter(ProfessionParameter.ACTION_TYPE, Actions.TNT_DESTROY)
                    .addParameter(ProfessionParameter.THIS_BLOCKSTATE, state)
                    .addParameter(ProfessionParameter.BLOCKPOS, blockPos)
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
}

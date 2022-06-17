package com.epherical.professions.triggers;

import com.epherical.professions.ProfessionsForge;
import com.epherical.professions.events.BrewPotionEvent;
import com.epherical.professions.events.EnchantedItemEvent;
import com.epherical.professions.events.SmeltItemEvent;
import com.epherical.professions.profession.ProfessionContext;
import com.epherical.professions.profession.ProfessionParameter;
import com.epherical.professions.profession.action.Actions;
import com.epherical.professions.util.EnchantmentContainer;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.ExplosionEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.Map;
import java.util.UUID;

public class BlockTriggers {

    private final ProfessionsForge mod;

    public BlockTriggers(ProfessionsForge mod) {
        this.mod = mod;
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onBlockBreak(BlockEvent.BreakEvent event) {
        if (event.isCanceled()) {
            return;
        }
        if (event.getWorld() instanceof ServerLevel level) {
            ProfessionContext.Builder builder = new ProfessionContext.Builder(level)
                    .addRandom(level.random)
                    .addParameter(ProfessionParameter.ACTION_TYPE, Actions.BREAK_BLOCK)
                    .addParameter(ProfessionParameter.BLOCKPOS, event.getPos())
                    .addParameter(ProfessionParameter.THIS_PLAYER, mod.getPlayerManager().getPlayer(event.getPlayer().getUUID()))
                    .addParameter(ProfessionParameter.THIS_BLOCKSTATE, event.getState())
                    .addParameter(ProfessionParameter.TOOL, event.getPlayer().getMainHandItem());
            RewardHandler.handleReward(builder);
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onBlockPlace(BlockEvent.EntityPlaceEvent event) {
        if (event.isCanceled()) {
            return;
        }
        if (event.getWorld() instanceof ServerLevel level && event.getEntity() instanceof ServerPlayer player) {
            ProfessionContext.Builder builder = new ProfessionContext.Builder(level)
                    .addRandom(level.random)
                    .addParameter(ProfessionParameter.ACTION_TYPE, Actions.PLACE_BLOCK)
                    .addParameter(ProfessionParameter.BLOCKPOS, event.getPos())
                    .addParameter(ProfessionParameter.THIS_BLOCKSTATE, event.getPlacedBlock())
                    .addParameter(ProfessionParameter.THIS_PLAYER, mod.getPlayerManager().getPlayer(player.getUUID()));
            RewardHandler.handleReward(builder);
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onExplosion(ExplosionEvent.Detonate event) {
        if (event.getWorld() instanceof ServerLevel level && event.getExplosion().getSourceMob() instanceof ServerPlayer player) {
            // This could possibly be slow, in fabric we just use a mixin to directly get the blockstate in the final explosion.
            for (BlockPos affectedBlock : event.getAffectedBlocks()) {
                ProfessionContext.Builder builder = new ProfessionContext.Builder(level)
                        .addRandom(level.random)
                        .addParameter(ProfessionParameter.ACTION_TYPE, Actions.TNT_DESTROY)
                        .addParameter(ProfessionParameter.BLOCKPOS, affectedBlock)
                        .addParameter(ProfessionParameter.THIS_BLOCKSTATE, level.getBlockState(affectedBlock))
                        .addParameter(ProfessionParameter.THIS_PLAYER, mod.getPlayerManager().getPlayer(player.getUUID()));
                RewardHandler.handleReward(builder);
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onSmeltItem(SmeltItemEvent event) {
        BlockEntity blockEntity = event.getBlockEntity();
        ItemStack smeltedItem = event.getSmeltedItem();
        UUID owner = event.getOwner();
        Recipe<?> recipe = event.getRecipe();
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
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onBrew(BrewPotionEvent event) {
        BlockEntity blockEntity = event.getBlockEntity();
        UUID owner = event.getPlacedBy();
        ItemStack brewingIngredient = event.getBrewingIngredient();
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
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onEnchant(EnchantedItemEvent event) {
        ServerLevel level = event.getPlayer().getLevel();
        ServerPlayer player = event.getPlayer();
        ItemStack itemEnchanted = event.getItemStack();
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
    }

}

package com.epherical.professions.triggers;

import com.epherical.professions.ProfessionsForge;
import com.epherical.professions.events.EnchantedItemEvent;
import com.epherical.professions.profession.ProfessionContext;
import com.epherical.professions.profession.ProfessionParameter;
import com.epherical.professions.profession.action.Actions;
import com.epherical.professions.util.EnchantmentContainer;
import com.epherical.professions.util.mixins.PlayerOwnable;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.event.brewing.PlayerBrewedPotionEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.ExplosionEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.Map;

public class BlockTriggers {

    private final ProfessionsForge mod;
    public static Capability<PlayerOwnable> OWNING_CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {});

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
                    .addParameter(ProfessionParameter.ACTION_TYPE, Actions.BREAK_BLOCK.get())
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
                    .addParameter(ProfessionParameter.ACTION_TYPE, Actions.PLACE_BLOCK.get())
                    .addParameter(ProfessionParameter.BLOCKPOS, event.getPos())
                    .addParameter(ProfessionParameter.THIS_BLOCKSTATE, event.getPlacedBlock()) // todo: test
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
                        .addParameter(ProfessionParameter.ACTION_TYPE, Actions.TNT_DESTROY.get())
                        .addParameter(ProfessionParameter.THIS_BLOCKSTATE, level.getBlockState(affectedBlock))
                        .addParameter(ProfessionParameter.THIS_PLAYER, mod.getPlayerManager().getPlayer(player.getUUID()));
                RewardHandler.handleReward(builder);
            }
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
            builder.addParameter(ProfessionParameter.ACTION_TYPE, Actions.ENCHANT_ITEM.get())
                    .addParameter(ProfessionParameter.ENCHANTMENT, new EnchantmentContainer(entry.getKey(), entry.getValue()));
            RewardHandler.handleReward(builder);
        }

        builder = new ProfessionContext.Builder(level)
                .addRandom(level.random)
                .addParameter(ProfessionParameter.THIS_PLAYER, mod.getPlayerManager().getPlayer(player.getUUID()))
                .addParameter(ProfessionParameter.ACTION_TYPE, Actions.ENCHANT_ITEM.get())
                .addParameter(ProfessionParameter.ITEM_INVOLVED, itemEnchanted);
        RewardHandler.handleReward(builder);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onBrew(PlayerBrewedPotionEvent event) {

    }

}

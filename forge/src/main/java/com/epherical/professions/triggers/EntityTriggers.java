package com.epherical.professions.triggers;

import com.epherical.professions.ProfessionsForge;
import com.epherical.professions.events.TradeWithVillagerEvent;
import com.epherical.professions.profession.ProfessionContext;
import com.epherical.professions.profession.ProfessionParameter;
import com.epherical.professions.profession.action.Actions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.TradeWithVillager;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.RecipeHolder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraftforge.event.entity.living.AnimalTameEvent;
import net.minecraftforge.event.entity.living.BabyEntitySpawnEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.ItemFishedEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.village.VillagerTradesEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class EntityTriggers {

    private ProfessionsForge mod;

    public EntityTriggers(ProfessionsForge mod) {
        this.mod = mod;
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onKillEntity(LivingDeathEvent event) {
        if (event.isCanceled() || event.getEntity().level.isClientSide) {
            return;
        }
        ServerLevel level = (ServerLevel) event.getEntity().getLevel();
        Entity source = event.getSource().getEntity();
        LivingEntity killedEntity = event.getEntityLiving();
        ProfessionContext.Builder builder = new ProfessionContext.Builder(level)
                .addRandom(level.random)
                .addParameter(ProfessionParameter.ACTION_TYPE, Actions.KILL_ENTITY.get());
        if (source instanceof Player) {
            if (killedEntity instanceof AgeableMob mob) {
                if (!mob.isBaby()) {
                    builder.addParameter(ProfessionParameter.THIS_PLAYER, mod.getPlayerManager().getPlayer(source.getUUID()))
                            .addParameter(ProfessionParameter.ENTITY, killedEntity);
                }
            } else {
                builder.addParameter(ProfessionParameter.THIS_PLAYER, mod.getPlayerManager().getPlayer(source.getUUID()))
                        .addParameter(ProfessionParameter.ENTITY, killedEntity);
            }
            RewardHandler.handleReward(builder);
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onCatchFish(ItemFishedEvent event) {
        Player player = event.getPlayer();
        if (event.isCanceled() || player.level.isClientSide) {
            return;
        }
        ServerLevel level = (ServerLevel) event.getPlayer().level;
        ProfessionContext.Builder builder = new ProfessionContext.Builder(level)
                .addRandom(level.random)
                .addParameter(ProfessionParameter.THIS_PLAYER, mod.getPlayerManager().getPlayer(player.getUUID()));
        for (ItemStack drop : event.getDrops()) {
            builder.addParameter(ProfessionParameter.ACTION_TYPE, Actions.FISH_ACTION.get())
                    .addParameter(ProfessionParameter.ITEM_INVOLVED, drop);
            RewardHandler.handleReward(builder);
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onCraftItem(PlayerEvent.ItemCraftedEvent event) {
        Player player = event.getPlayer();
        if (event.isCanceled() || player.level.isClientSide) {
            return;
        }
        Recipe<?> recipe = null;
        if (event.getInventory() instanceof RecipeHolder holder) {
            recipe = holder.getRecipeUsed();
        }
        ServerLevel level = (ServerLevel) player.getLevel();
        ProfessionContext.Builder builder = new ProfessionContext.Builder(level)
                .addRandom(level.random)
                .addParameter(ProfessionParameter.THIS_PLAYER, mod.getPlayerManager().getPlayer(player.getUUID()))
                .addParameter(ProfessionParameter.ACTION_TYPE, Actions.CRAFTS_ITEM.get())
                .addParameter(ProfessionParameter.ITEM_INVOLVED, event.getCrafting())
                .addParameter(ProfessionParameter.RECIPE_CRAFTED, recipe);
        RewardHandler.handleReward(builder);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onTakeSmeltedItem(PlayerEvent.ItemSmeltedEvent event) {
        Player player = event.getPlayer();
        if (event.isCanceled() || player.level.isClientSide) {
            return;
        }
        ServerLevel level = (ServerLevel) player.getLevel();

        ProfessionContext.Builder builder = new ProfessionContext.Builder(level)
                .addRandom(level.random)
                .addParameter(ProfessionParameter.THIS_PLAYER, mod.getPlayerManager().getPlayer(player.getUUID()))
                .addParameter(ProfessionParameter.ACTION_TYPE, Actions.TAKE_COOKED_ITEM.get())
                .addParameter(ProfessionParameter.ITEM_INVOLVED, event.getSmelting());
        RewardHandler.handleReward(builder);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onBreedAnimal(BabyEntitySpawnEvent event) {
        Player player = event.getCausedByPlayer();
        if (event.isCanceled() || player == null || player.getLevel().isClientSide) {
            return;
        }
        ServerLevel level = (ServerLevel) player.getLevel();
        ProfessionContext.Builder builder = new ProfessionContext.Builder(level)
                .addRandom(level.random)
                .addParameter(ProfessionParameter.THIS_PLAYER, mod.getPlayerManager().getPlayer(player.getUUID()))
                .addParameter(ProfessionParameter.ACTION_TYPE, Actions.BREED_ENTITY.get())
                .addParameter(ProfessionParameter.ENTITY, event.getChild());
        RewardHandler.handleReward(builder);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onTameAnimal(AnimalTameEvent event) {
        Player player = event.getTamer();
        if (event.isCanceled() || player.getLevel().isClientSide) {
            return;
        }
        ServerLevel level = (ServerLevel) player.getLevel();
        ProfessionContext.Builder builder = new ProfessionContext.Builder(level)
                .addRandom(level.random)
                .addParameter(ProfessionParameter.THIS_PLAYER, mod.getPlayerManager().getPlayer(player.getUUID()))
                .addParameter(ProfessionParameter.ACTION_TYPE, Actions.TAME_ENTITY.get())
                .addParameter(ProfessionParameter.ENTITY, event.getAnimal());
        RewardHandler.handleReward(builder);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onVillagerTrade(TradeWithVillagerEvent event) {
        ServerPlayer player = event.getPlayer();
        ServerLevel level = player.getLevel();
        AbstractVillager villager = event.getVillager();
        MerchantOffer offer = event.getOffer();
        ProfessionContext.Builder builder = new ProfessionContext.Builder(level)
                .addRandom(level.random)
                .addParameter(ProfessionParameter.THIS_PLAYER, mod.getPlayerManager().getPlayer(player.getUUID()))
                .addParameter(ProfessionParameter.ACTION_TYPE, Actions.VILLAGER_TRADE.get())
                .addParameter(ProfessionParameter.ENTITY, villager)
                .addParameter(ProfessionParameter.ITEM_INVOLVED, offer.getCostA());
        RewardHandler.handleReward(builder);
        builder.addParameter(ProfessionParameter.ITEM_INVOLVED, offer.getCostB());
        RewardHandler.handleReward(builder);
    }
}

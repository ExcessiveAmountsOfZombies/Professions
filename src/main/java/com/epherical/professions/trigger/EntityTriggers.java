package com.epherical.professions.trigger;

import com.epherical.professions.ProfessionsMod;
import com.epherical.professions.events.trigger.TriggerEvents;
import com.epherical.professions.profession.ProfessionContext;
import com.epherical.professions.profession.ProfessionParameter;
import com.epherical.professions.profession.action.Actions;
import net.fabricmc.fabric.api.entity.event.v1.ServerEntityCombatEvents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;

public class EntityTriggers {

    public static void init(ProfessionsMod mod) {
        ServerEntityCombatEvents.AFTER_KILLED_OTHER_ENTITY.register((world, entity, killedEntity) -> {
            if (entity instanceof Player) {
                ProfessionContext.Builder builder = new ProfessionContext.Builder(world)
                        .addRandom(world.random)
                        .addParameter(ProfessionParameter.ACTION_TYPE, Actions.KILL_ENTITY)
                        .addParameter(ProfessionParameter.THIS_PLAYER, mod.getPlayerManager().getPlayer(entity.getUUID()))
                        .addParameter(ProfessionParameter.ENTITY, killedEntity);
                RewardHandler.handleReward(builder.build());
            }
        });

        TriggerEvents.CATCH_FISH_EVENT.register((player, stack) -> {
            ServerLevel level = player.getLevel();
            ProfessionContext.Builder builder = new ProfessionContext.Builder(level)
                    .addRandom(level.random)
                    .addParameter(ProfessionParameter.THIS_PLAYER, mod.getPlayerManager().getPlayer(player.getUUID()))
                    .addParameter(ProfessionParameter.ACTION_TYPE, Actions.FISH_ACTION)
                    .addParameter(ProfessionParameter.ITEM_INVOLVED, stack);
            RewardHandler.handleReward(builder.build());
        });

        TriggerEvents.CRAFT_ITEM_EVENT.register((player, stack, recipe) -> {
            ServerLevel level = player.getLevel();
            ProfessionContext.Builder builder = new ProfessionContext.Builder(level)
                    .addRandom(level.random)
                    .addParameter(ProfessionParameter.THIS_PLAYER, mod.getPlayerManager().getPlayer(player.getUUID()))
                    .addParameter(ProfessionParameter.ACTION_TYPE, Actions.CRAFTS_ITEM)
                    .addParameter(ProfessionParameter.ITEM_INVOLVED, stack)
                    .addParameter(ProfessionParameter.RECIPE_CRAFTED, recipe);
            RewardHandler.handleReward(builder.build());
        });

        TriggerEvents.TAKE_SMELTED_ITEM_EVENT.register((player, stack) -> {
            ServerLevel level = player.getLevel();
            ProfessionContext.Builder builder = new ProfessionContext.Builder(level)
                    .addRandom(level.random)
                    .addParameter(ProfessionParameter.THIS_PLAYER, mod.getPlayerManager().getPlayer(player.getUUID()))
                    .addParameter(ProfessionParameter.ACTION_TYPE, Actions.TAKE_COOKED_ITEM)
                    .addParameter(ProfessionParameter.ITEM_INVOLVED, stack);
            RewardHandler.handleReward(builder.build());
        });

        TriggerEvents.BREED_ANIMAL_EVENT.register((player, parent, partner, child) -> {
            ServerLevel level = player.getLevel();
            ProfessionContext.Builder builder = new ProfessionContext.Builder(level)
                    .addRandom(level.random)
                    .addParameter(ProfessionParameter.THIS_PLAYER, mod.getPlayerManager().getPlayer(player.getUUID()))
                    .addParameter(ProfessionParameter.ACTION_TYPE, Actions.BREED_ENTITY)
                    .addParameter(ProfessionParameter.ENTITY, child);
            RewardHandler.handleReward(builder.build());
        });

        TriggerEvents.TAME_ANIMAL_EVENT.register((player, animal) -> {
            ServerLevel level = player.getLevel();
            ProfessionContext.Builder builder = new ProfessionContext.Builder(level)
                    .addRandom(level.random)
                    .addParameter(ProfessionParameter.THIS_PLAYER, mod.getPlayerManager().getPlayer(player.getUUID()))
                    .addParameter(ProfessionParameter.ACTION_TYPE, Actions.TAME_ENTITY)
                    .addParameter(ProfessionParameter.ENTITY, animal);
            RewardHandler.handleReward(builder.build());
        });

        TriggerEvents.VILLAGER_TRADE_EVENT.register((player, villager, offer) -> {
            ServerLevel level = player.getLevel();
            ProfessionContext.Builder builder = new ProfessionContext.Builder(level)
                    .addRandom(level.random)
                    .addParameter(ProfessionParameter.THIS_PLAYER, mod.getPlayerManager().getPlayer(player.getUUID()))
                    .addParameter(ProfessionParameter.ACTION_TYPE, Actions.VILLAGER_TRADE)
                    .addParameter(ProfessionParameter.ENTITY, villager)
                    .addParameter(ProfessionParameter.ITEM_INVOLVED, offer.getCostA());
            RewardHandler.handleReward(builder.build());
            builder.addParameter(ProfessionParameter.ITEM_INVOLVED, offer.getCostB());
            RewardHandler.handleReward(builder.build());

        });
    }
}

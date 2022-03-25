package com.epherical.professions.trigger;

import com.epherical.professions.PlayerManager;
import com.epherical.professions.events.trigger.TriggerEvents;
import com.epherical.professions.profession.ProfessionContext;
import com.epherical.professions.profession.ProfessionParameter;
import com.epherical.professions.profession.action.Actions;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.server.level.ServerLevel;

public class BlockTriggers {

    public static void init(PlayerManager manager) {
        PlayerBlockBreakEvents.AFTER.register((world, player, pos, state, blockEntity) -> {
            // only ever run on the server.
            if (world instanceof ServerLevel) {
                ProfessionContext.Builder builder = new ProfessionContext.Builder((ServerLevel) world)
                        .addRandom(world.random)
                        .addParameter(ProfessionParameter.ACTION_TYPE, Actions.BREAK_BLOCK)
                        .addParameter(ProfessionParameter.BLOCKPOS, pos)
                        .addParameter(ProfessionParameter.THIS_PLAYER, manager.getPlayer(player.getUUID()))
                        .addParameter(ProfessionParameter.THIS_BLOCKSTATE, state)
                        .addParameter(ProfessionParameter.TOOL, player.getMainHandItem());
                RewardHandler.handleReward(builder.build());
            }
        });

        TriggerEvents.PLACE_BLOCK_EVENT.register((player, state, pos) -> {
            ServerLevel level = player.getLevel();
            ProfessionContext.Builder builder = new ProfessionContext.Builder(level)
                    .addRandom(level.random)
                    .addParameter(ProfessionParameter.ACTION_TYPE, Actions.PLACE_BLOCK)
                    .addParameter(ProfessionParameter.THIS_BLOCKSTATE, state)
                    .addParameter(ProfessionParameter.THIS_PLAYER, manager.getPlayer(player.getUUID()));
            RewardHandler.handleReward(builder.build());
        });

        TriggerEvents.TNT_DESTROY_EVENT.register((source, state) -> {
            ServerLevel level = source.getLevel();
            ProfessionContext.Builder builder = new ProfessionContext.Builder(level)
                    .addRandom(level.random)
                    .addParameter(ProfessionParameter.ACTION_TYPE, Actions.TNT_DESTROY)
                    .addParameter(ProfessionParameter.THIS_BLOCKSTATE, state)
                    .addParameter(ProfessionParameter.THIS_PLAYER, manager.getPlayer(source.getUUID()));
            RewardHandler.handleReward(builder.build());
        });

        TriggerEvents.SMELT_ITEM_EVENT.register((owner, smeltedItem, recipe, blockEntity) -> {
            if (blockEntity.getLevel() != null && !blockEntity.getLevel().isClientSide) {
                ServerLevel level = (ServerLevel) blockEntity.getLevel();
                ProfessionContext.Builder builder = new ProfessionContext.Builder(level)
                        .addRandom(level.random)
                        .addParameter(ProfessionParameter.THIS_PLAYER, manager.getPlayer(owner))
                        .addParameter(ProfessionParameter.ACTION_TYPE, Actions.ON_ITEM_COOK)
                        .addParameter(ProfessionParameter.ITEM_INVOLVED, smeltedItem)
                        .addParameter(ProfessionParameter.THIS_BLOCKSTATE, blockEntity.getBlockState())
                        .addParameter(ProfessionParameter.RECIPE_CRAFTED, recipe);
                RewardHandler.handleReward(builder.build());
            }
        });
    }
}

package com.epherical.professions.trigger;

import com.epherical.professions.PlayerManager;
import com.epherical.professions.events.trigger.TriggerEvents;
import com.epherical.professions.profession.ProfessionContext;
import com.epherical.professions.profession.ProfessionParameter;
import com.epherical.professions.profession.action.Actions;
import net.fabricmc.fabric.api.entity.event.v1.ServerEntityCombatEvents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;

public class EntityTriggers {

    public static void init(PlayerManager manager) {
        ServerEntityCombatEvents.AFTER_KILLED_OTHER_ENTITY.register((world, entity, killedEntity) -> {
            if (entity instanceof Player) {
                ProfessionContext.Builder builder = new ProfessionContext.Builder(world)
                        .addRandom(world.random)
                        .addParameter(ProfessionParameter.ACTION_TYPE, Actions.KILL_ENTITY)
                        .addParameter(ProfessionParameter.THIS_PLAYER, manager.getPlayer(entity.getUUID()))
                        .addParameter(ProfessionParameter.ENTITY, killedEntity);
                RewardHandler.handleReward(builder.build());
            }
        });

        TriggerEvents.CATCH_FISH_EVENT.register((player, stack) -> {
            ServerLevel level = player.getLevel();
            ProfessionContext.Builder builder = new ProfessionContext.Builder(level)
                    .addRandom(level.random)
                    .addParameter(ProfessionParameter.THIS_PLAYER, manager.getPlayer(player.getUUID()))
                    .addParameter(ProfessionParameter.ACTION_TYPE, Actions.FISH_ACTION)
                    .addParameter(ProfessionParameter.ITEM_INVOLVED, stack);
            RewardHandler.handleReward(builder.build());
        });
    }
}

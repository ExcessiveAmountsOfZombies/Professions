package com.epherical.professions.trigger;

import com.epherical.professions.PlayerManager;
import com.epherical.professions.profession.ProfessionContext;
import com.epherical.professions.profession.ProfessionParameter;
import com.epherical.professions.profession.action.Actions;
import net.fabricmc.fabric.api.entity.event.v1.ServerEntityCombatEvents;
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
    }
}

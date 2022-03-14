package com.epherical.professions.trigger;

import com.epherical.professions.PlayerManager;
import com.epherical.professions.profession.ProfessionContext;
import com.epherical.professions.profession.ProfessionParameter;
import com.epherical.professions.profession.action.ProfessionActions;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.server.level.ServerLevel;

public class BlockBreakTrigger {

    public static void init(PlayerManager manager) {
        PlayerBlockBreakEvents.AFTER.register((world, player, pos, state, blockEntity) -> {
            // only ever run on the server.
            if (world instanceof ServerLevel) {
                ProfessionContext.Builder builder = new ProfessionContext.Builder((ServerLevel) world)
                        .addRandom(world.random)
                        .addParameter(ProfessionParameter.ACTION_TYPE, ProfessionActions.BREAK_BLOCK)
                        .addParameter(ProfessionParameter.BLOCKPOS, pos)
                        .addParameter(ProfessionParameter.THIS_PLAYER, manager.getPlayer(player.getUUID()))
                        .addParameter(ProfessionParameter.TOOL, player.getMainHandItem());
                RewardHandler.handleReward(builder.build());
            }
        });
    }
}

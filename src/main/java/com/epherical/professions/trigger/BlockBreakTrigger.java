package com.epherical.professions.trigger;

import com.epherical.professions.profession.ProfessionContext;
import com.epherical.professions.profession.ProfessionParameter;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.server.level.ServerLevel;

public class BlockBreakTrigger {

    public static void init() {
        PlayerBlockBreakEvents.AFTER.register((world, player, pos, state, blockEntity) -> {
            // only ever run on the server.
            if (world instanceof ServerLevel) {
                ProfessionContext.Builder builder = new ProfessionContext.Builder((ServerLevel) world)
                        .addRandom(world.random)
                        .addParameter(ProfessionParameter.BLOCKPOS, pos)
                        .addParameter(ProfessionParameter.THIS_PLAYER, player)
                        .addParameter(ProfessionParameter.TOOL, player.getMainHandItem());
                builder.
            }
        });
    }
}

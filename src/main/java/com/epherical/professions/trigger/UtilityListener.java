package com.epherical.professions.trigger;

import com.epherical.professions.ProfessionsMod;
import com.epherical.professions.events.trigger.TriggerEvents;
import com.epherical.professions.util.mixins.PlayerOwnable;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;

public class UtilityListener {

    public static void init(ProfessionsMod mod) {
        TriggerEvents.PLACE_BLOCK_EVENT.register((player, state, pos) -> {
            ServerLevel level = player.getLevel().getLevel();
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof PlayerOwnable owned) {
                owned.professions$setPlacedBy(player);
            }
        });
    }
}

package com.epherical.professions.events.trigger;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.state.BlockState;

public final class TriggerEvents {

    public static final Event<PlaceBlock> PLACE_BLOCK_EVENT = EventFactory.createArrayBacked(PlaceBlock.class, calls -> (player, state) -> {
        for (PlaceBlock call : calls) {
            call.onBlockPlace(player, state);
        }
    });



    public interface PlaceBlock {
        /**
         * This event is already assumed to take place on the server, no side checks are needed.
         */
        void onBlockPlace(ServerPlayer player, BlockState state);
    }

}

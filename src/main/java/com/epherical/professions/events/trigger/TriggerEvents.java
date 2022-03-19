package com.epherical.professions.events.trigger;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.state.BlockState;

public final class TriggerEvents {

    public static final Event<PlaceBlock> PLACE_BLOCK_EVENT = EventFactory.createArrayBacked(PlaceBlock.class, calls -> (player, state) -> {
        for (PlaceBlock call : calls) {
            call.onBlockPlace(player, state);
        }
    });

    public static final Event<TNTDestroy> TNT_DESTROY_EVENT = EventFactory.createArrayBacked(TNTDestroy.class, calls -> (source, state) -> {
        for (TNTDestroy call : calls) {
            call.onTNTDestroy(source, state);
        }
    });



    public interface PlaceBlock {
        /**
         * This event is already assumed to take place on the server, no side checks are needed.
         */
        void onBlockPlace(ServerPlayer player, BlockState state);
    }

    public interface TNTDestroy {
        void onTNTDestroy(ServerPlayer source, BlockState state);
    }

}

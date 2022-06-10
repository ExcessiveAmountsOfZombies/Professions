package com.epherical.professions.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.state.BlockState;

public class SyncEvents {

    public static final Event<AbortDestroyBlockEvent> ABORTED_BLOCK_DESTROY_EVENT = EventFactory.createArrayBacked(AbortDestroyBlockEvent.class, calls ->
            (pos, player, state, level, action) -> {
        for (AbortDestroyBlockEvent call : calls) {
            call.onBlockDestroyAborted(pos, player, state, level, action);
        }
    });



    public interface AbortDestroyBlockEvent {
        void onBlockDestroyAborted(BlockPos pos, ServerPlayer player, BlockState state, ServerLevel level, ServerboundPlayerActionPacket.Action action);
    }


}

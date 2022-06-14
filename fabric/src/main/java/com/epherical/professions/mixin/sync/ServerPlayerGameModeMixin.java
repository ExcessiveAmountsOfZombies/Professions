package com.epherical.professions.mixin.sync;

import com.epherical.professions.events.SyncEvents;
import com.epherical.professions.trigger.UtilityListener;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerPlayerGameMode;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerGameMode.class)
public class ServerPlayerGameModeMixin {

    @Shadow
    @Final
    protected ServerPlayer player;

    @Shadow
    protected ServerLevel level;

    @Shadow
    private boolean hasDelayedDestroy;

    @Inject(method = "handleBlockBreakAction",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;destroyBlockProgress(ILnet/minecraft/core/BlockPos;I)V", ordinal = 3))
    public void professions$breakAborted(BlockPos blockPos, ServerboundPlayerActionPacket.Action action, Direction direction, int i, CallbackInfo ci) {
        SyncEvents.ABORTED_BLOCK_DESTROY_EVENT.invoker().onBlockDestroyAborted(blockPos, this.player, this.level.getBlockState(blockPos), this.level, action);
    }

    @Inject(method = "handleBlockBreakAction", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;isAir()Z", ordinal = 2))
    public void professions$breakDestroyed(BlockPos blockPos, ServerboundPlayerActionPacket.Action action, Direction direction, int i, CallbackInfo ci) {
        SyncEvents.ABORTED_BLOCK_DESTROY_EVENT.invoker().onBlockDestroyAborted(blockPos, this.player, this.level.getBlockState(blockPos), this.level, action);
    }

    /**
     * Because we can't perfectly stop vanilla clients, they may be able to get partially through breaking a block, and if they do manage to break it,
     * the server turns on hasDelayedDestroy, which will continue incrementing block break progress even if the player stops. as long as the player
     * continues to hold the item that's 'locked' this is fine, but not ideal, but if they swap to their hand, or an unlocked item, the block will break.
     * So if we know they've delayDestroyed a block, and they did it with a locked item, we can just set hasDelayedDestroy to false.
     */
    @Inject(method = "tick", at = @At("HEAD"))
    public void professions$onTick(CallbackInfo ci) {
        if (hasDelayedDestroy && UtilityListener.playerDestroyedWithLockedItem.remove(player.getUUID())) {
            hasDelayedDestroy = false;
        }
    }

}

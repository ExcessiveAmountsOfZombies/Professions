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

    @Shadow @Final protected ServerPlayer player;

    @Shadow protected ServerLevel level;

    @Shadow private boolean hasDelayedDestroy;

    @Shadow private boolean isDestroyingBlock;

    @Inject(method = "handleBlockBreakAction",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;destroyBlockProgress(ILnet/minecraft/core/BlockPos;I)V", ordinal = 3))
    public void professions$breakAborted(BlockPos blockPos, ServerboundPlayerActionPacket.Action action, Direction direction, int i, CallbackInfo ci) {
        SyncEvents.ABORTED_BLOCK_DESTROY_EVENT.invoker().onBlockDestroyAborted(blockPos, this.player, this.level.getBlockState(blockPos), this.level, action);
    }

    @Inject(method = "handleBlockBreakAction", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;isAir()Z", ordinal = 2))
    public void professions$breakDestroyed(BlockPos blockPos, ServerboundPlayerActionPacket.Action action, Direction direction, int i, CallbackInfo ci) {
        SyncEvents.ABORTED_BLOCK_DESTROY_EVENT.invoker().onBlockDestroyAborted(blockPos, this.player, this.level.getBlockState(blockPos), this.level, action);
    }

    @Inject(method = "tick", at = @At("HEAD"))
    public void onTick(CallbackInfo ci) {
        if (hasDelayedDestroy && UtilityListener.playerDestroyedWithLockedItem.remove(player.getUUID())) {
            hasDelayedDestroy = false;
        }
    }

}

package com.epherical.professions.mixin;

import com.epherical.professions.events.trigger.TriggerEvents;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectListIterator;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(Explosion.class)
public abstract class TntExplosionTriggerMixin {

    @Shadow
    @Nullable
    public abstract LivingEntity getSourceMob();

    @Inject(method = "finalizeExplosion", locals = LocalCapture.CAPTURE_FAILHARD,
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z"))
    public void onExploded(boolean spawnParticles, CallbackInfo ci, boolean blz, ObjectArrayList list, boolean bl, ObjectListIterator iterator, BlockPos blockPos, BlockState blockState) {
        if (getSourceMob() != null && getSourceMob() instanceof ServerPlayer) {
            TriggerEvents.TNT_DESTROY_EVENT.invoker().onTNTDestroy((ServerPlayer) getSourceMob(), blockState, blockPos);
        }
    }
}

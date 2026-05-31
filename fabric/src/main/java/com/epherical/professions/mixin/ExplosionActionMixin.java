package com.epherical.professions.mixin;

import com.epherical.professions.FabricProfessionsMod;
import com.epherical.professions.api.IProfessionalPlayer;
import com.epherical.professions.bootstrap.Actions;
import com.epherical.professions.core.context.ProfessionContext;
import com.epherical.professions.core.context.ProfessionParameter;
import it.unimi.dsi.fastutil.objects.ObjectListIterator;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(Explosion.class)
public class ExplosionActionMixin {

    @Shadow
    @Final
    private Level level;

    @Inject(method = "finalizeExplosion",
            locals = LocalCapture.CAPTURE_FAILHARD,
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;setBlockAndUpdate(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)Z"))
    private void onFinalizeExplosion(boolean spawnParticles, CallbackInfo ci, boolean bl, ObjectListIterator var3, BlockPos blockPos2) {
        if (!(this.level instanceof ServerLevel serverLevel)) {
            return;
        }

        Explosion explosion = (Explosion) (Object) this;
        LivingEntity source = explosion.getIndirectSourceEntity();
        if (!(source instanceof ServerPlayer player) || !explosion.interactsWithBlocks()) {
            return;
        }

        FabricProfessionsMod mod = FabricProfessionsMod.mod;
        if (mod == null) {
            return;
        }

        IProfessionalPlayer professionalPlayer = FabricProfessionsMod.ensureProfessionalPlayer(mod, player);
        if (professionalPlayer == null) {
            return;
        }

        BlockState blockState = serverLevel.getBlockState(blockPos2);
        if (blockState.isAir()) {
            return;
        }

        ProfessionContext.Builder builder = ProfessionContext.builder(serverLevel, Actions.BLOCK_EXPLODE, professionalPlayer)
                .addParameter(ProfessionParameter.BLOCKPOS, blockPos2)
                .addParameter(ProfessionParameter.THIS_BLOCK_STATE, blockState)
                .addParameter(ProfessionParameter.THIS_HOLDER, blockState.getBlockHolder());
        mod.getPlayerManager().processAction(player, builder.build());
    }
}

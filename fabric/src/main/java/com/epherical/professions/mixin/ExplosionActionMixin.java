package com.epherical.professions.mixin;

import com.epherical.professions.FabricProfessionsMod;
import com.epherical.professions.api.IProfessionalPlayer;
import com.epherical.professions.bootstrap.Actions;
import com.epherical.professions.core.context.ProfessionContext;
import com.epherical.professions.core.context.ProfessionParameter;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.BiConsumer;

@Mixin(BlockBehaviour.class)
public class ExplosionActionMixin {

    @Inject(method = "onExplosionHit",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/server/level/ServerLevel;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z"))
    private void onExplosionHit(BlockState state, ServerLevel level, BlockPos pos, Explosion explosion,
                                BiConsumer<ItemStack, BlockPos> onHit, CallbackInfo ci) {
        LivingEntity source = explosion.getIndirectSourceEntity();
        if (!(source instanceof ServerPlayer player)) {
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

        ProfessionContext.Builder builder = ProfessionContext.builder(level, Actions.BLOCK_EXPLODE, professionalPlayer)
                .addParameter(ProfessionParameter.BLOCKPOS, pos)
                .addParameter(ProfessionParameter.THIS_BLOCK_STATE, state)
                .addParameter(ProfessionParameter.THIS_HOLDER, state.typeHolder());
        mod.getPlayerManager().processAction(player, builder.build());
    }
}

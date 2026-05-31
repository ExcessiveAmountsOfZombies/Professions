package com.epherical.professions.mixin;

import com.epherical.professions.FabricProfessionsMod;
import com.epherical.professions.api.IProfessionalPlayer;
import com.epherical.professions.bootstrap.Actions;
import com.epherical.professions.core.context.ProfessionContext;
import com.epherical.professions.core.context.ProfessionParameter;
import net.minecraft.advancements.critereon.ItemUsedOnLocationTrigger;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemUsedOnLocationTrigger.class)
public class BlockItemPlacementMixin {

    @Inject(method = "trigger", at = @At("HEAD"))
    private void onPlace(ServerPlayer player, BlockPos pos, ItemStack stack, CallbackInfo ci) {
        ServerLevel serverLevel = player.serverLevel();
        FabricProfessionsMod mod = FabricProfessionsMod.mod;

        if (player.isCreative()) {
            return;
        }

        if (mod == null) {
            return;
        }

        IProfessionalPlayer professionalPlayer = FabricProfessionsMod.ensureProfessionalPlayer(mod, player);
        if (professionalPlayer == null) {
            return;
        }

        BlockState blockState = serverLevel.getBlockState(pos);
        if (blockState.isAir()) {
            return;
        }

        ProfessionContext.Builder builder = ProfessionContext.builder(serverLevel, Actions.BLOCK_PLACE, professionalPlayer)
                .addParameter(ProfessionParameter.THIS_BLOCK_STATE, blockState)
                .addParameter(ProfessionParameter.BLOCKPOS, pos)
                .addParameter(ProfessionParameter.THIS_HOLDER, blockState.getBlockHolder());
        mod.getPlayerManager().processAction(player, builder.build());
    }
}

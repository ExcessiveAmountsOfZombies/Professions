package com.epherical.professions.mixin;

import com.epherical.professions.NeoForgeBlockEntityAttachments;
import com.epherical.professions.NeoForgeProfessionsMod;
import com.epherical.professions.PlayerManager;
import com.epherical.professions.api.IProfessionalPlayer;
import com.epherical.professions.bootstrap.Actions;
import com.epherical.professions.core.context.ProfessionContext;
import com.epherical.professions.core.context.ProfessionParameter;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BrewingStandBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;

@Mixin(BrewingStandBlockEntity.class)
public class NFBrewingStandMixin {

    @Inject(method = "serverTick", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/level/block/entity/BrewingStandBlockEntity;doBrew(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/NonNullList;)V"))
    private static void professions2DoBrew(Level level, BlockPos pos, BlockState state, BrewingStandBlockEntity blockEntity, CallbackInfo ci,
                                           @Local(ordinal = 1) ItemStack ingredientStack) {
        if (!(level instanceof ServerLevel serverLevel)) {
            return;
        }

        UUID placedBy = NeoForgeBlockEntityAttachments.getPlacedBy(blockEntity);
        if (placedBy == null) {
            return;
        }

        ServerPlayer player = serverLevel.getServer().getPlayerList().getPlayer(placedBy);
        if (player == null) {
            return;
        }

        PlayerManager playerManager = NeoForgeProfessionsMod.mod.getPlayerManager();
        IProfessionalPlayer professionalPlayer = playerManager.getPlayer(placedBy);
        if (professionalPlayer == null || ingredientStack.isEmpty()) {
            return;
        }

        ProfessionContext.Builder builder = ProfessionContext.builder(serverLevel, Actions.BREW_ACTION, professionalPlayer)
                .addParameter(ProfessionParameter.ITEM_INVOLVED, ingredientStack.copy());
        playerManager.processAction(player, builder.build());
    }
}

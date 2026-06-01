package com.epherical.professions.mixin;

import com.epherical.professions.FabricProfessionsMod;
import com.epherical.professions.api.IProfessionalPlayer;
import com.epherical.professions.bootstrap.Actions;
import com.epherical.professions.core.context.ProfessionContext;
import com.epherical.professions.core.context.ProfessionParameter;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.FurnaceResultSlot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FurnaceResultSlot.class)
public class SmeltTakeActionMixin {

    @Inject(method = "onTake", at = @At("HEAD"))
    private void onSmeltResultTaken(Player player, ItemStack stack, CallbackInfo ci) {
        if (!(player instanceof ServerPlayer serverPlayer)) {
            return;
        }

        FabricProfessionsMod mod = FabricProfessionsMod.mod;
        if (mod == null) {
            return;
        }

        IProfessionalPlayer professionalPlayer = FabricProfessionsMod.ensureProfessionalPlayer(mod, serverPlayer);
        if (professionalPlayer == null) {
            return;
        }

        ProfessionContext.Builder builder = ProfessionContext.builder(serverPlayer.level(), Actions.SMELT_TAKE_ACTION, professionalPlayer)
                .addParameter(ProfessionParameter.ITEM_INVOLVED, stack);
        mod.getPlayerManager().processAction(serverPlayer, builder.build());
    }
}

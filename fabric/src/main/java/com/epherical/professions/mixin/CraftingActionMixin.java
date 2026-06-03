package com.epherical.professions.mixin;

import com.epherical.professions.FabricProfessionsMod;
import com.epherical.professions.api.IProfessionalPlayer;
import com.epherical.professions.bootstrap.Actions;
import com.epherical.professions.core.context.ProfessionContext;
import com.epherical.professions.core.context.ProfessionParameter;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ResultSlot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ResultSlot.class)
public class CraftingActionMixin {

    @Shadow
    @Final
    private Player player;

    @Inject(method = "checkTakeAchievements", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/inventory/RecipeCraftingHolder;awardUsedRecipes(Lnet/minecraft/world/entity/player/Player;Ljava/util/List;)V"))
    private void onCraftResultTaken(ItemStack stack, CallbackInfo ci) {
        if (!(player instanceof ServerPlayer serverPlayer)) {
            return;
        }

        FabricProfessionsMod mod = FabricProfessionsMod.mod;
        if (mod == null) {
            return;
        }

        IProfessionalPlayer professionalPlayer = mod.ensureProfessionalPlayer(serverPlayer);
        if (professionalPlayer == null) {
            return;
        }

        ProfessionContext.Builder builder = ProfessionContext.builder(serverPlayer.serverLevel(), Actions.CRAFTING_ACTION, professionalPlayer)
                .addParameter(ProfessionParameter.ITEM_INVOLVED, stack);
        mod.getPlayerManager().processAction(serverPlayer, builder.build());
    }
}

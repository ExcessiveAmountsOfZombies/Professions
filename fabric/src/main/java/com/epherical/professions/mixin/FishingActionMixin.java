package com.epherical.professions.mixin;

import com.epherical.professions.FabricProfessionsMod;
import com.epherical.professions.api.IProfessionalPlayer;
import com.epherical.professions.bootstrap.Actions;
import com.epherical.professions.core.context.ProfessionContext;
import com.epherical.professions.core.context.ProfessionParameter;
import net.minecraft.advancements.critereon.FishingRodHookedTrigger;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Collection;

@Mixin(FishingRodHookedTrigger.class)
public class FishingActionMixin {

    @Inject(method = "trigger", at = @At("HEAD"))
    private void onFishingRodHooked(ServerPlayer player, ItemStack rod, FishingHook entity, Collection<ItemStack> stacks, CallbackInfo ci) {
        if (stacks.isEmpty()) {
            return;
        }

        FabricProfessionsMod mod = FabricProfessionsMod.mod;
        if (mod == null) {
            return;
        }

        IProfessionalPlayer professionalPlayer = mod.ensureProfessionalPlayer(player);
        if (professionalPlayer == null) {
            return;
        }

        for (ItemStack drop : stacks) {
            ProfessionContext.Builder builder = ProfessionContext.builder(player.serverLevel(), Actions.FISHING_ACTION, professionalPlayer)
                    .addParameter(ProfessionParameter.ITEM_INVOLVED, drop);
            mod.getPlayerManager().processAction(player, builder.build());
        }
    }
}

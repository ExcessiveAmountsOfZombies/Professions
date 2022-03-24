package com.epherical.professions.mixin;

import com.epherical.professions.events.trigger.TriggerEvents;
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
public class FishingMixin {

    @Inject(method = "trigger", at = @At("HEAD"))
    public void onCatchFish(ServerPlayer player, ItemStack rod, FishingHook entity, Collection<ItemStack> stacks, CallbackInfo ci) {
        for (ItemStack stack : stacks) {
            TriggerEvents.CATCH_FISH_EVENT.invoker().onCatchFish(player, stack);
        }
    }

}

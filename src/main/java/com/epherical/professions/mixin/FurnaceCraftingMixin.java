package com.epherical.professions.mixin;

import com.epherical.professions.events.trigger.TriggerEvents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.FurnaceResultSlot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FurnaceResultSlot.class)
public class FurnaceCraftingMixin {

    @Shadow @Final private Player player;

    @Inject(method = "checkTakeAchievements", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/entity/AbstractFurnaceBlockEntity;awardUsedRecipesAndPopExperience(Lnet/minecraft/server/level/ServerPlayer;)V"))
    public void whenItemTaken(ItemStack stack, CallbackInfo ci) {
        TriggerEvents.TAKE_SMELTED_ITEM_EVENT.invoker().onItemTake((ServerPlayer) player, stack);
    }
}

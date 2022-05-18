package com.epherical.professions.mixin;

import com.epherical.professions.events.trigger.TriggerEvents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.RecipeHolder;
import net.minecraft.world.inventory.ResultSlot;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ResultSlot.class)
public class CraftingMixin extends Slot {

    @Shadow @Final private Player player;

    public CraftingMixin(Container container, int i, int j, int k) {
        super(container, i, j, k);
    }

    @Inject(method = "checkTakeAchievements", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/inventory/RecipeHolder;awardUsedRecipes(Lnet/minecraft/world/entity/player/Player;)V"))
    public void onRecipeCrafted(ItemStack stack, CallbackInfo ci) {
        if (!this.player.level.isClientSide) {

            TriggerEvents.CRAFT_ITEM_EVENT.invoker().onCraftItem((ServerPlayer) this.player, stack, ((RecipeHolder) this.container).getRecipeUsed());
        }
    }

}

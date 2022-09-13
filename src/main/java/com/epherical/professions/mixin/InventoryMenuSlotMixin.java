package com.epherical.professions.mixin;

import com.epherical.professions.CommonPlatform;
import com.epherical.professions.api.ProfessionalPlayer;
import com.epherical.professions.profession.unlock.Unlock;
import com.epherical.professions.profession.unlock.Unlocks;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Set;

@Mixin(targets = "net.minecraft.world.inventory.InventoryMenu$1")
public class InventoryMenuSlotMixin {

    @Inject(method = "mayPlace(Lnet/minecraft/world/item/ItemStack;)Z", at = @At("HEAD"), cancellable = true)
    public void professions$unlockableArmor(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        Slot slot = (Slot) (Object) this;
        Inventory inventory = (Inventory) slot.container;
        if (inventory.player instanceof ServerPlayer serverPlayer) {
            ProfessionalPlayer player = CommonPlatform.platform.getPlayerManager().getPlayer(serverPlayer.getUUID());
            if (player == null) {
                return;
            }
            List<Unlock.Singular<Item>> lockedKnowledge = player.getLockedKnowledge(stack.getItem(), Set.of(Unlocks.EQUIPMENT_UNLOCK, Unlocks.ADVANCEMENT_UNLOCK));
            for (Unlock.Singular<Item> itemSingular : lockedKnowledge) {
                if (!itemSingular.canUse(player)) {
                    cir.setReturnValue(false);
                }
            }
        }
    }

}

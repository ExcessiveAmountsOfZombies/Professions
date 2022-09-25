package com.epherical.professions.mixin.unlocks;

import com.epherical.professions.CommonPlatform;
import com.epherical.professions.api.ProfessionalPlayer;
import com.epherical.professions.profession.unlock.Unlock;
import com.epherical.professions.profession.unlock.Unlocks;
import com.mojang.authlib.GameProfile;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Set;

@Mixin(Player.class)
public abstract class PlayerEquipmentMixin {

    @Shadow
    @Final
    private GameProfile gameProfile;

    @Shadow
    public abstract boolean addItem(ItemStack stack);

    @Shadow
    public abstract @Nullable ItemEntity drop(ItemStack itemStack, boolean includeThrowerName);

    @Inject(method = "setItemSlot", at = @At(value = "HEAD"), cancellable = true)
    public void professions$unlockPreventEquip(EquipmentSlot slot, ItemStack itemStack, CallbackInfo ci) {
        if (slot.getType() == EquipmentSlot.Type.ARMOR) {
            ProfessionalPlayer professionalPlayer = CommonPlatform.platform.getPlayerManager().getPlayer(this.gameProfile.getId());
            if (professionalPlayer != null) {
                List<Unlock.Singular<Item>> lockedKnowledge = professionalPlayer.getLockedKnowledge(itemStack.getItem(), Set.of(Unlocks.EQUIPMENT_UNLOCK, Unlocks.ADVANCEMENT_UNLOCK));
                for (Unlock.Singular<Item> itemSingular : lockedKnowledge) {
                    if (!itemSingular.canUse(professionalPlayer)) {
                        // It doesnt' seem particularly ideal to do it this way, but items likely copied before they get here
                        // meaning that if we don't do anything, the item just vanishes...
                        if (!this.addItem(itemStack)) {
                            this.drop(itemStack, false);
                        }
                        ci.cancel();
                    }
                }
            }
        }
    }
}

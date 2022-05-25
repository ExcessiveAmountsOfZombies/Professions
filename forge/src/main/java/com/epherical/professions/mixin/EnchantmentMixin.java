package com.epherical.professions.mixin;

import com.epherical.professions.events.EnchantedItemEvent;
import net.minecraft.advancements.critereon.EnchantedItemTrigger;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EnchantedItemTrigger.class)
public class EnchantmentMixin {

    @Inject(method = "trigger", at = @At("HEAD"))
    public void onTrigger(ServerPlayer player, ItemStack item, int levelsSpent, CallbackInfo ci) {
        MinecraftForge.EVENT_BUS.post(new EnchantedItemEvent(player, item, levelsSpent));
    }
}
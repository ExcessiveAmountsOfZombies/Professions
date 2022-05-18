package com.epherical.professions.mixin;

import com.epherical.professions.events.trigger.TriggerEvents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.trading.MerchantOffer;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractVillager.class)
public class VillagerTradeMixin {

    @Shadow private @Nullable Player tradingPlayer;

    @Inject(method = "notifyTrade", at = @At(value = "INVOKE", target = "Lnet/minecraft/advancements/critereon/TradeTrigger;trigger(Lnet/minecraft/server/level/ServerPlayer;Lnet/minecraft/world/entity/npc/AbstractVillager;Lnet/minecraft/world/item/ItemStack;)V"))
    public void onTrade(MerchantOffer offer, CallbackInfo ci) {
        TriggerEvents.VILLAGER_TRADE_EVENT.invoker().onTradeWithVillager((ServerPlayer) this.tradingPlayer, (AbstractVillager)(Object) this, offer);
    }
}

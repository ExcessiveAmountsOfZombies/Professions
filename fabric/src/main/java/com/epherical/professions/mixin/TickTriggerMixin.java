package com.epherical.professions.mixin;

import com.epherical.professions.events.trigger.TriggerEvents;
import net.minecraft.advancements.critereon.PlayerTrigger;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerTrigger.class)
public class TickTriggerMixin {


    @Inject(method = "trigger", at = @At("HEAD"))
    public void professions$tickTrigger(ServerPlayer player, CallbackInfo ci) {
        TriggerEvents.PLAYER_LOCATION_EVENT.invoker().onPlayerTick(player);
    }
}

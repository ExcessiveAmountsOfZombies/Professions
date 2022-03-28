package com.epherical.professions.mixin;

import com.epherical.professions.events.trigger.TriggerEvents;
import net.minecraft.advancements.critereon.TameAnimalTrigger;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.animal.Animal;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TameAnimalTrigger.class)
public class TamingMixin {

    @Inject(method = "trigger", at = @At("HEAD"))
    public void onTame(ServerPlayer player, Animal entity, CallbackInfo ci) {
        TriggerEvents.TAME_ANIMAL_EVENT.invoker().onTame(player, entity);
    }
}

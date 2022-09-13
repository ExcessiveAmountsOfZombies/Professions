package com.epherical.professions.mixin;

import com.epherical.professions.events.trigger.TriggerEvents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerPlayerGameMode;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerPlayerGameMode.class)
public class ItemUseOnMixin {

    @Inject(method = "useItemOn", at = @At("HEAD"), cancellable = true)
    public void professions$unlockableInteractions(ServerPlayer player, Level level, ItemStack stack,
                                                   InteractionHand hand, BlockHitResult hitResult, CallbackInfoReturnable<InteractionResult> cir) {
        InteractionResult result = TriggerEvents.PLAYER_USE_ITEM_ON_EVENT.invoker().onPlayerUse(player, level, stack, hand, hitResult);
        if (result != InteractionResult.PASS) {
            cir.setReturnValue(result);
            cir.cancel();
            return;
        }


    }
}

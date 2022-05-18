package com.epherical.professions.mixin;

import com.epherical.professions.events.trigger.TriggerEvents;
import net.minecraft.advancements.critereon.PlacedBlockTrigger;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlacedBlockTrigger.class)
public class PlaceBlockTriggerMixin {

    /**
     * We're making an assumption here that everyone else that might interact with block placing has already done potential
     * cancellations before this point.
     */
    @Inject(method = "trigger", at = @At("HEAD"))
    public void onTrigger(ServerPlayer player, BlockPos pos, ItemStack item, CallbackInfo ci) {
        TriggerEvents.PLACE_BLOCK_EVENT.invoker().onBlockPlace(player, player.getLevel().getBlockState(pos), pos);
    }

}

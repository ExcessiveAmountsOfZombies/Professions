package com.epherical.professions.mixin.ownables;

import com.epherical.professions.events.trigger.TriggerEvents;
import com.epherical.professions.integration.cardinal.BlockEntityComponent;
import com.epherical.professions.integration.cardinal.PlayerOwning;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BrewingStandBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BrewingStandBlockEntity.class)
public class BrewingStandBlockEntityOwnableMixin {

    @Inject(method = "serverTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/entity/BrewingStandBlockEntity;doBrew(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/NonNullList;)V"))
    private static void professions$brew(Level level, BlockPos pos, BlockState state, BrewingStandBlockEntity blockEntity, CallbackInfo ci) {
        PlayerOwning component = blockEntity.getComponent(BlockEntityComponent.PLAYER_OWNABLE);
        if (component.hasOwner()) {
            TriggerEvents.BREW_POTION_EVENT.invoker().onPotionBrew(component.getPlacedBy(), blockEntity.getItem(3), blockEntity);
        }
    }
    /*@Inject(method = "doBrew", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;shrink(I)V"))
    private static void professions$brew(Level level, BlockPos pos, NonNullList<ItemStack> nonNullList, CallbackInfo ci) {

    }*/
}

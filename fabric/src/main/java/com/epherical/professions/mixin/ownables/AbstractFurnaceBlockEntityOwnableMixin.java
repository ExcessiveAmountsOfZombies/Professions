package com.epherical.professions.mixin.ownables;

import com.epherical.professions.events.trigger.TriggerEvents;
import com.epherical.professions.integration.cardinal.BlockEntityComponent;
import com.epherical.professions.integration.cardinal.PlayerOwning;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(AbstractFurnaceBlockEntity.class)
public class AbstractFurnaceBlockEntityOwnableMixin {


    @Inject(method = "serverTick", locals = LocalCapture.CAPTURE_FAILEXCEPTION,
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/entity/AbstractFurnaceBlockEntity;setRecipeUsed(Lnet/minecraft/world/item/crafting/Recipe;)V"))
    private static void onSuccessfulSmelt(Level level, BlockPos pos, BlockState state, AbstractFurnaceBlockEntity blockEntity, CallbackInfo ci, boolean bl, boolean bl2, ItemStack itemStack, Recipe<?> recipe, int i) {
        PlayerOwning component = blockEntity.getComponent(BlockEntityComponent.PLAYER_OWNABLE);
        if (component.hasOwner()) {
            TriggerEvents.SMELT_ITEM_EVENT.invoker().onItemSmelt(component.getPlacedBy(), blockEntity.getItem(2), recipe, blockEntity);
        }
    }
}

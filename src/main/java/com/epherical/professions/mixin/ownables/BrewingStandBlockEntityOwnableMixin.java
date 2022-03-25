package com.epherical.professions.mixin.ownables;

import com.epherical.professions.events.trigger.TriggerEvents;
import com.epherical.professions.util.PlayerOwnable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BrewingStandBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;

@Mixin(BrewingStandBlockEntity.class)
public class BrewingStandBlockEntityOwnableMixin implements PlayerOwnable {

    @Shadow private NonNullList<ItemStack> items;
    @Unique UUID professions$placedBy;

    @Override
    public UUID professions$getPlacedBy() {
        return professions$placedBy;
    }

    @Override
    public void professions$setPlacedBy(ServerPlayer player) {
        this.professions$placedBy = player.getUUID();
    }

    @Inject(method = "load", at = @At("TAIL"))
    public void onLoad(CompoundTag tag, CallbackInfo ci) {
        if (tag.contains("pf_owid")) {
            // TODO: maybe make this a config option to either have it be persistent or non-persistent.
            professions$placedBy = tag.getUUID("OwnerID");
        }
    }

    @Inject(method = "saveAdditional", at = @At("TAIL"))
    public void onSave(CompoundTag tag, CallbackInfo ci) {
        if (professions$placedBy != null) {
            tag.putUUID("pf_owid", professions$placedBy);
        }
    }

    @Inject(method = "serverTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/entity/BrewingStandBlockEntity;doBrew(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/NonNullList;)V"))
    private static void professions$brew(Level level, BlockPos pos, BlockState state, BrewingStandBlockEntity blockEntity, CallbackInfo ci) {
        if (((PlayerOwnable) blockEntity).professions$hasOwner()) {
            TriggerEvents.BREW_POTION_EVENT.invoker().onPotionBrew(((PlayerOwnable) blockEntity).professions$getPlacedBy(), blockEntity.getItem(3), blockEntity);
        }

    }
    /*@Inject(method = "doBrew", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;shrink(I)V"))
    private static void professions$brew(Level level, BlockPos pos, NonNullList<ItemStack> nonNullList, CallbackInfo ci) {

    }*/
}

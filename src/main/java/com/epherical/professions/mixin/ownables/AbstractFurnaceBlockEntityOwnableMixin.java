package com.epherical.professions.mixin.ownables;

import com.epherical.professions.events.trigger.TriggerEvents;
import com.epherical.professions.util.PlayerOwnable;
import net.fabricmc.fabric.mixin.item.client.ClientPlayerInteractionManagerMixin;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.UUID;

@Mixin(AbstractFurnaceBlockEntity.class)
public abstract class AbstractFurnaceBlockEntityOwnableMixin extends BlockEntity implements PlayerOwnable {

    @Shadow protected NonNullList<ItemStack> items;
    @Unique UUID professions$placedBy;

    public AbstractFurnaceBlockEntityOwnableMixin(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState) {
        super(blockEntityType, blockPos, blockState);
    }

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

    @Inject(method = "serverTick", locals = LocalCapture.CAPTURE_FAILEXCEPTION,
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/entity/AbstractFurnaceBlockEntity;setRecipeUsed(Lnet/minecraft/world/item/crafting/Recipe;)V"))
    private static void onSuccessfulSmelt(Level level, BlockPos pos, BlockState state, AbstractFurnaceBlockEntity blockEntity, CallbackInfo ci, boolean bl, boolean bl2, ItemStack itemStack, Recipe<?> recipe, int i) {
        if (((PlayerOwnable) blockEntity).professions$hasOwner()) {
            TriggerEvents.SMELT_ITEM_EVENT.invoker().onItemSmelt(((PlayerOwnable) blockEntity).professions$getPlacedBy(), blockEntity.getItem(2), recipe, blockEntity);
        }
    }
}

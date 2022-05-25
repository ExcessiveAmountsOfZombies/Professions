package com.epherical.professions.mixin;

import com.epherical.professions.capability.PlayerOwnable;
import com.epherical.professions.capability.impl.PlayerOwnableImpl;
import com.epherical.professions.events.SmeltItemEvent;
import com.epherical.professions.util.mixins.CapabilityMixinHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.LazyOptional;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;


@Mixin(AbstractFurnaceBlockEntity.class)
public class ProfessionsOwnableAbstractFurnaceMixin implements CapabilityMixinHelper<PlayerOwnable> {

    @Unique
    private LazyOptional<PlayerOwnable> cachedOwnable;

    @Inject(method = "serverTick", locals = LocalCapture.CAPTURE_FAILEXCEPTION,
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/entity/AbstractFurnaceBlockEntity;setRecipeUsed(Lnet/minecraft/world/item/crafting/Recipe;)V"))
    private static void onSuccessfulSmelt(Level level, BlockPos pos, BlockState state, AbstractFurnaceBlockEntity blockEntity, CallbackInfo ci, boolean bl, boolean bl2, ItemStack itemStack, Recipe<?> recipe, int i) {
        CapabilityMixinHelper<PlayerOwnable> entityCast = (CapabilityMixinHelper<PlayerOwnable>) blockEntity;
        if (entityCast.professions$getValue() == null) {
            LazyOptional<PlayerOwnable> capability = blockEntity.getCapability(PlayerOwnableImpl.OWNING_CAPABILITY);
            entityCast.professions$setValue(capability);
        }

        entityCast.professions$getValue().ifPresent(playerOwnable -> {
            if (playerOwnable.hasOwner()) {
                MinecraftForge.EVENT_BUS.post(new SmeltItemEvent(playerOwnable.getPlacedBy(), blockEntity.getItem(2), recipe, blockEntity));
            }
        });
    }

    @Override
    public LazyOptional<PlayerOwnable> professions$getValue() {
        return cachedOwnable;
    }

    @Override
    public void professions$setValue(LazyOptional<PlayerOwnable> value) {
        this.cachedOwnable = value;
    }
}

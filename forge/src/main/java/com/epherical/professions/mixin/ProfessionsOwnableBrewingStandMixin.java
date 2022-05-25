package com.epherical.professions.mixin;

import com.epherical.professions.capability.PlayerOwnable;
import com.epherical.professions.capability.impl.PlayerOwnableImpl;
import com.epherical.professions.events.BrewPotionEvent;
import com.epherical.professions.util.mixins.CapabilityMixinHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BrewingStandBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.LazyOptional;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(BrewingStandBlockEntity.class)
public class ProfessionsOwnableBrewingStandMixin implements CapabilityMixinHelper<PlayerOwnable> {

    @Unique
    private LazyOptional<PlayerOwnable> cachedOwnable;

    @Inject(method = "serverTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/entity/BrewingStandBlockEntity;doBrew(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/NonNullList;)V"))
    private static void professions$brew(Level level, BlockPos pos, BlockState state, BrewingStandBlockEntity blockEntity, CallbackInfo ci) {
        CapabilityMixinHelper<PlayerOwnable> entityCast = (CapabilityMixinHelper<PlayerOwnable>) blockEntity;
        if (entityCast.professions$getValue() == null) {
            LazyOptional<PlayerOwnable> capability = blockEntity.getCapability(PlayerOwnableImpl.OWNING_CAPABILITY);
            entityCast.professions$setValue(capability);
        }

        entityCast.professions$getValue().ifPresent(playerOwnable -> {
            if (playerOwnable.hasOwner()) {
                MinecraftForge.EVENT_BUS.post(new BrewPotionEvent(playerOwnable.getPlacedBy(), blockEntity.getItem(3), blockEntity));
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

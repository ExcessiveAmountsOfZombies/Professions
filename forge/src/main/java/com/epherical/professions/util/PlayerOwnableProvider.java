package com.epherical.professions.util;

import com.epherical.professions.Constants;
import com.epherical.professions.util.mixins.PlayerOwnable;
import com.epherical.professions.util.mixins.PlayerOwnableImpl;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PlayerOwnableProvider implements ICapabilityProvider, INBTSerializable<CompoundTag> {
    public static final ResourceLocation ID = Constants.modID("pf_owid");

    private final PlayerOwnable backing = new PlayerOwnableImpl();
    private final LazyOptional<PlayerOwnable> optional = LazyOptional.of(() -> backing);


    @NotNull
    @Override
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        return PlayerOwnableImpl.OWNING_CAPABILITY.orEmpty(cap, this.optional);
    }

    @Override
    public CompoundTag serializeNBT() {
        return this.backing.serializeNBT();
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        this.backing.deserializeNBT(nbt);
    }
}

package com.epherical.professions.util.mixins;

import com.epherical.professions.Constants;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class PlayerOwnableImpl implements PlayerOwnable {

    public static Capability<PlayerOwnable> OWNING_CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {});

    private UUID placedUUID;

    @Override
    public UUID getPlacedBy() {
        return placedUUID;
    }

    @Override
    public void setPlacedBy(ServerPlayer player) {
        this.placedUUID = player.getUUID();
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        if (placedUUID != null) {
            tag.putUUID("pf_owid", placedUUID);
        }
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        if (nbt.contains("pf_owid")) {
            this.placedUUID = nbt.getUUID("pf_owid");
        }
    }
}

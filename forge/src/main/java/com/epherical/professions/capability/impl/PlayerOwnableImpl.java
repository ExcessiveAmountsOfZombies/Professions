package com.epherical.professions.capability.impl;

import com.epherical.professions.capability.PlayerOwnable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;

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

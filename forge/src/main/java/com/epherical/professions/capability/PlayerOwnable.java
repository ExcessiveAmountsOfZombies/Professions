package com.epherical.professions.capability;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.util.INBTSerializable;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public interface PlayerOwnable extends INBTSerializable<CompoundTag> {

    UUID getPlacedBy();

    void setPlacedBy(ServerPlayer player);

    default boolean hasOwner() {
        return getPlacedBy() != null;
    }

    @Nullable
    default Player professions$getPlayer(Level level) {
        if (getPlacedBy() != null) {
            return level.getPlayerByUUID(getPlacedBy());
        }
        return null;
    }
}

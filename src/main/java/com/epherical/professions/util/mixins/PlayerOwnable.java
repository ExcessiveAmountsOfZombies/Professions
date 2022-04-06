package com.epherical.professions.util.mixins;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public interface PlayerOwnable {

    UUID professions$getPlacedBy();

    void professions$setPlacedBy(ServerPlayer player);

    default boolean professions$hasOwner() {
        return professions$getPlacedBy() != null;
    }

    @Nullable
    default Player professions$getPlayer(Level level) {
        if (professions$getPlacedBy() != null) {
            return level.getPlayerByUUID(professions$getPlacedBy());
        }
        return null;
    }
}

package com.epherical.professions.api;

import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public interface IProfessionalPlayer {

    UUID getUUID();






    @Nullable ServerPlayer getPlayer();



    void setNeedsToBeSaved();

    void save();


    // todo;
    void handleAction();


}

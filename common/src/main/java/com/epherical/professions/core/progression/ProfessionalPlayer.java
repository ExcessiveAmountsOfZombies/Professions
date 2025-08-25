package com.epherical.professions.core.progression;

import com.epherical.professions.api.IProfessionalPlayer;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

public class ProfessionalPlayer implements IProfessionalPlayer {

    private UUID uuid;
    private final List<Occupation> occupations;


    public ProfessionalPlayer(List<Occupation> occupations) {
        this.occupations = occupations;
    }




    @Override
    public UUID getUUID() {
        return null;
    }

    @Override
    public @Nullable ServerPlayer getPlayer() {
        return null;
    }

    @Override
    public void setNeedsToBeSaved() {

    }

    @Override
    public void save() {

    }

    @Override
    public void handleAction() {

    }
}

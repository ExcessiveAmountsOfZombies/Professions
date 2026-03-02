package com.epherical.professions.api;

import com.epherical.professions.core.Profession;
import com.epherical.professions.core.context.ProfessionContext;
import com.epherical.professions.core.progression.Occupation;
import com.epherical.professions.core.progression.OccupationSlot;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerPlayer;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

public interface IProfessionalPlayer {

    @Nullable
    UUID getUUID();

    @Nullable ServerPlayer getPlayer();

    void setPlayer(@Nullable ServerPlayer player);

    void setDirty(boolean dirty);

    boolean isDirty();


    <T> void handleAction(ProfessionContext context, Holder<T> holder);

    boolean alreadyHasOccupation(Holder<Profession> profession);

    boolean isOccupationActive(Holder<Profession> profession);

    boolean joinOccupation(Holder<Profession> profession, OccupationSlot slot);

    boolean leaveOccupation(Holder<Profession> profession);

    boolean fireFromOccupation(Holder<Profession> profession);

    Occupation getOccupation(Holder<Profession> profession);

    void updateOccupationPerks();

    List<Occupation> getActiveOccupations();

    List<Occupation> getInactiveOccupations();

    List<Occupation> getAllOccupations();


}

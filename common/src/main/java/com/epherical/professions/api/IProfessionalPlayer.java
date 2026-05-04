package com.epherical.professions.api;

import com.epherical.professions.core.Profession;
import com.epherical.professions.core.ProfessionCategory;
import com.epherical.professions.model.Occupation;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.player.Player;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

public interface IProfessionalPlayer {

    @Nullable
    UUID getUUID();

    @Nullable Player getPlayer();

    void setPlayer(@Nullable Player player);

    void markDirty(boolean dirty);

    void setCategory(ProfessionCategory category);

    @Nullable
    ProfessionCategory getCategory();

    boolean isDirty();

    boolean alreadyHasOccupation(Holder<Profession> profession);

    boolean isOccupationActive(Holder<Profession> profession);

    @Nullable
    Occupation getOccupation(Holder<Profession> profession);

    void updateOccupationPerks();

    /**
     * @return Will return ALL occupations, regardless of them being active or not. Mainly to make sure they can be saved.
     */
    List<Occupation> getAllOccupations();

    /**
     *
     * @return Only returns the active occupations
     */
    List<Occupation> getActiveOccupations();


}

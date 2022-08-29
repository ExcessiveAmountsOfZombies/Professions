package com.epherical.professions.profession.modifiers.perks;

import com.epherical.professions.api.ProfessionalPlayer;
import com.epherical.professions.profession.progression.Occupation;
import net.minecraft.server.level.ServerPlayer;

public interface Perk {

    PerkType getType();

    int getLevel();

    boolean canApplyPerkToPlayer(String permission, ProfessionalPlayer context, Occupation occupation);

    void applyPerkToPlayer(ServerPlayer player, Occupation occupation);

    void removeOldPerkData(ServerPlayer player, Occupation occupation);

    @FunctionalInterface
    interface Builder {
        Perk build();
    }
}

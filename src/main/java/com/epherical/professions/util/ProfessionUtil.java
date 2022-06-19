package com.epherical.professions.util;

import com.epherical.professions.api.ProfessionalPlayer;
import com.epherical.professions.api.UnlockableData;
import com.epherical.professions.profession.progression.Occupation;
import com.epherical.professions.profession.unlock.Unlock;
import com.epherical.professions.profession.unlock.UnlockType;
import com.mojang.datafixers.util.Pair;

import java.util.List;

public class ProfessionUtil {

    /**
     * Checks all potential unlocks on the player, and then determines if any of them are false.
     * Don't use if you want to know specifically which one is keeping you from doing an action.
     * @return true if it can be used, false if not.
     */
    public static <T> boolean canUse(ProfessionalPlayer player, UnlockType<T> type, T object) {
        List<Unlock.Singular<T>> unlocks = player.getLockedKnowledge(type, object);
        for (Unlock.Singular<T> unlock : unlocks) {
            if (!unlock.canUse(player)) {
                return false;
            }
        }
        return true;
    }
}

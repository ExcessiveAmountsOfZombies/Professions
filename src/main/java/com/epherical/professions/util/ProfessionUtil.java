package com.epherical.professions.util;

import com.epherical.professions.api.ProfessionalPlayer;
import com.epherical.professions.api.UnlockableData;
import com.epherical.professions.profession.unlock.Unlock;
import com.epherical.professions.profession.unlock.UnlockType;
import com.mojang.datafixers.util.Pair;

public class ProfessionUtil {


    /**
     * @return If the player can use, pair right is true, pair left can determine what makes it true.
     * If the data could not be found, it falls back to true. If the player can use it because they meet the condition,
     * the unlock is paired. If the player did not meet the condition, the pair unlock and false is returned.
     */
    public static <T> Pair<Unlock.Singular<T>, Boolean> canUse(ProfessionalPlayer player, UnlockType<T> type, T object) {
        UnlockableData data = player.getUnlockableData(type, object);
        if (data == null) {
            return Pair.of(null, true);
        }
        Unlock.Singular<T> unlock = data.getUnlock(object);
        if (unlock == null) {
            return Pair.of(null, true);
        }
        if (data.canUse(unlock, object).valid()) {
            return Pair.of(unlock, true);
        }

        return Pair.of(unlock, false);
    }


}

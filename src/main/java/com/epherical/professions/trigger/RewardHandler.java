package com.epherical.professions.trigger;

import com.epherical.professions.ProfessionsMod;
import com.epherical.professions.api.ProfessionalPlayer;
import com.epherical.professions.profession.ProfessionContext;
import com.epherical.professions.profession.ProfessionParameter;

public class RewardHandler {

    public static void handleReward(ProfessionContext context) {
        ProfessionalPlayer pPlayer = ProfessionsMod.getInstance().getDataStorage().getUser(context.getParameter(ProfessionParameter.THIS_PLAYER).getUUID());
        if (pPlayer != null) {
            pPlayer.handleAction(context);
        }
    }
}

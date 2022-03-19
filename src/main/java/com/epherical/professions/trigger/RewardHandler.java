package com.epherical.professions.trigger;

import com.epherical.professions.api.ProfessionalPlayer;
import com.epherical.professions.profession.ProfessionContext;
import com.epherical.professions.profession.ProfessionParameter;

public class RewardHandler {

    public static void handleReward(ProfessionContext context) {
        ProfessionalPlayer pPlayer = context.getParameter(ProfessionParameter.THIS_PLAYER);
        pPlayer.handleAction(context);
    }
}

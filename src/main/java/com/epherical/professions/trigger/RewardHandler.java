package com.epherical.professions.trigger;

import com.epherical.professions.api.ProfessionalPlayer;
import com.epherical.professions.profession.ProfessionContext;
import net.minecraft.world.scores.Scoreboard;

public class RewardHandler {

    public static void handleReward(ProfessionContext context) {
        ProfessionalPlayer pPlayer;
        pPlayer.handleAction(context);
    }
}

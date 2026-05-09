package com.epherical.professions.listener.perks;

import com.epherical.professions.PerkManager;
import com.epherical.professions.api.IProfessionalPlayer;
import com.epherical.professions.api.event.EventListener;
import com.epherical.professions.api.event.runtime.rewards.OccupationLevelEvent;
import com.epherical.professions.model.Occupation;

public class PerkLevelListener implements EventListener<OccupationLevelEvent> {


    private final PerkManager perkManager;

    public PerkLevelListener(PerkManager manager) {
        this.perkManager = manager;
    }

    @Override
    public void handle(OccupationLevelEvent event) {
        IProfessionalPlayer player = event.getPlayer();
        Occupation occupation = event.getOccupation();
        int level = event.getNewLevel();


        if (player != null) {
            perkManager.setUnclaimedPerks(occupation, level, player);
        }
    }
}


package com.epherical.professions.listener.perks;

import com.epherical.professions.PerkManager;
import com.epherical.professions.api.event.EventListener;
import com.epherical.professions.api.event.runtime.PlayerJoinEvent;

public class PerkPlayerJoinListener implements EventListener<PlayerJoinEvent> {

    private final PerkManager perkManager;

    public PerkPlayerJoinListener(PerkManager perkManager) {
        this.perkManager = perkManager;
    }

    @Override
    public void handle(PlayerJoinEvent event) {
        perkManager.playerJoined(event.getPlayer(), event.getServerPlayer());
    }
}

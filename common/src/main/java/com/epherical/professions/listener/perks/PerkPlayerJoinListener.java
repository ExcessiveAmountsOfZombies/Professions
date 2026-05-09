package com.epherical.professions.listener.perks;

import com.epherical.professions.PerkManager;
import com.epherical.professions.ProfessionsCommon;
import com.epherical.professions.api.IProfessionalPlayer;
import com.epherical.professions.api.event.EventListener;
import com.epherical.professions.api.event.runtime.PlayerJoinEvent;
import com.epherical.professions.model.Occupation;
import com.epherical.professions.model.perks.IStartupPerk;
import com.epherical.professions.model.perks.Perk;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PerkPlayerJoinListener implements EventListener<PlayerJoinEvent> {

    private final PerkManager perkManager;

    public PerkPlayerJoinListener(PerkManager perkManager) {
        this.perkManager = perkManager;
    }

    @Override
    public void handle(PlayerJoinEvent event) {
        perkManager.playerJoined(event);
    }
}

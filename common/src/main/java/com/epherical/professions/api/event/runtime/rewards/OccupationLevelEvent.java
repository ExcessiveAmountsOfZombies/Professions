package com.epherical.professions.api.event.runtime.rewards;

import com.epherical.professions.ProfessionsCommon;
import com.epherical.professions.api.IProfessionalPlayer;
import com.epherical.professions.api.event.EventKey;
import com.epherical.professions.model.Occupation;
import com.epherical.professions.api.event.runtime.AbstractProfessionEvent;
import net.minecraft.resources.Identifier;

public class OccupationLevelEvent extends AbstractProfessionEvent {

    public static final EventKey<OccupationLevelEvent> KEY =
            new EventKey<>(Identifier.fromNamespaceAndPath(ProfessionsCommon.MOD_ID, "occupation_level"), OccupationLevelEvent.class);
    private final Occupation occupation;
    private final IProfessionalPlayer player;
    private final int oldLevel;
    private final int newLevel;


    public OccupationLevelEvent(Occupation occupation, int oldLevel, int newLevel, IProfessionalPlayer player) {
        super(KEY);
        this.occupation = occupation;
        this.oldLevel = oldLevel;
        this.newLevel = newLevel;
        this.player = player;
    }

    public IProfessionalPlayer getPlayer() {
        return player;
    }

    public Occupation getOccupation() {
        return occupation;
    }

    public int getOldLevel() {
        return oldLevel;
    }

    public int getNewLevel() {
        return newLevel;
    }
}

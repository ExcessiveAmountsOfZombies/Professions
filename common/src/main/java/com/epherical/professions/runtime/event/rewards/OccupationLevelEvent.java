package com.epherical.professions.runtime.event.rewards;

import com.epherical.professions.ProfessionsCommon;
import com.epherical.professions.api.event.EventKey;
import com.epherical.professions.core.context.ProfessionContext;
import com.epherical.professions.model.Occupation;
import com.epherical.professions.runtime.event.AbstractProfessionEvent;
import net.minecraft.resources.ResourceLocation;

public class OccupationLevelEvent extends AbstractProfessionEvent {

    public static final EventKey<OccupationLevelEvent> KEY =
            new EventKey<>(ResourceLocation.fromNamespaceAndPath(ProfessionsCommon.MOD_ID, "occupation_level"), OccupationLevelEvent.class);
    private final Occupation occupation;
    private final int oldLevel;
    private final int newLevel;


    public OccupationLevelEvent(Occupation occupation, int oldLevel, int newLevel) {
        super(KEY);
        this.occupation = occupation;
        this.oldLevel = oldLevel;
        this.newLevel = newLevel;
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

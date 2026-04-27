package com.epherical.professions.runtime.event.rewards;

import com.epherical.professions.ProfessionsCommon;
import com.epherical.professions.api.event.EventKey;
import com.epherical.professions.core.context.ProfessionContext;
import com.epherical.professions.model.Occupation;
import net.minecraft.resources.ResourceLocation;

public class OccupationExperienceEvent extends RewardEvent {

    public static final EventKey<OccupationExperienceEvent> KEY =
            new EventKey<>(ResourceLocation.fromNamespaceAndPath(ProfessionsCommon.MOD_ID, "reward_occupation_experience"), OccupationExperienceEvent.class);

    private final double baseAmount;

    private double newAmount;

    public OccupationExperienceEvent(ProfessionContext context, Occupation occupation, double baseAmount) {
        super(KEY, occupation, context);
        this.baseAmount = baseAmount;
        this.newAmount = baseAmount;
    }

    public double baseAmount() { return this.baseAmount; }

    public void setNewAmount(double newAmount) {
        this.newAmount = newAmount;
    }

    public double getNewAmount() {
        return newAmount;
    }
}

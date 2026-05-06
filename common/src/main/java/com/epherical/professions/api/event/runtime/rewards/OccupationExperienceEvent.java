package com.epherical.professions.api.event.runtime.rewards;

import com.epherical.professions.ProfessionsCommon;
import com.epherical.professions.api.event.EventKey;
import com.epherical.professions.core.context.ProfessionContext;
import com.epherical.professions.model.Occupation;
import com.epherical.professions.api.actions.Action;
import net.minecraft.resources.Identifier;

public class OccupationExperienceEvent extends RewardEvent {

    public static final EventKey<OccupationExperienceEvent> KEY =
            new EventKey<>(Identifier.fromNamespaceAndPath(ProfessionsCommon.MOD_ID, "reward_occupation_experience"), OccupationExperienceEvent.class);

    private final double baseAmount;

    private double newAmount;

    public OccupationExperienceEvent(ProfessionContext context, Action<?> action, Occupation occupation, double baseAmount) {
        super(KEY, occupation, action, context);
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

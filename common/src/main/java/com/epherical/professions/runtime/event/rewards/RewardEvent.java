package com.epherical.professions.runtime.event.rewards;

import com.epherical.professions.api.event.EventKey;
import com.epherical.professions.api.event.ProfessionEvent;
import com.epherical.professions.core.context.ProfessionContext;
import com.epherical.professions.model.Occupation;
import com.epherical.professions.runtime.event.AbstractCancellableProfessionEvent;

public abstract class RewardEvent extends AbstractCancellableProfessionEvent {


    private final Occupation occupation;
    private final ProfessionContext context;

    protected RewardEvent(EventKey<? extends ProfessionEvent> key, Occupation occupation,
                          ProfessionContext context) {
        super(key);
        this.occupation = occupation;
        this.context = context;
    }

    public Occupation getOccupation() {
        return occupation;
    }

    public ProfessionContext getContext() {
        return context;
    }

}

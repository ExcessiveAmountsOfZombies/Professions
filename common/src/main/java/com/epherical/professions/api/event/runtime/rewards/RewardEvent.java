package com.epherical.professions.api.event.runtime.rewards;

import com.epherical.professions.api.event.EventKey;
import com.epherical.professions.api.event.ProfessionEvent;
import com.epherical.professions.core.context.ProfessionContext;
import com.epherical.professions.model.Occupation;
import com.epherical.professions.model.actions.Action;
import com.epherical.professions.api.event.runtime.AbstractCancellableProfessionEvent;

public abstract class RewardEvent extends AbstractCancellableProfessionEvent {


    private final Occupation occupation;
    private final ProfessionContext context;
    private final Action<?> action;

    protected RewardEvent(EventKey<? extends ProfessionEvent> key, Occupation occupation, Action<?> action, ProfessionContext context) {
        super(key);
        this.occupation = occupation;
        this.action = action;
        this.context = context;
    }

    public Occupation getOccupation() {
        return occupation;
    }

    public ProfessionContext getContext() {
        return context;
    }

    public Action<?> getAction() {
        return action;
    }
}

package com.epherical.professions.runtime.event;

import com.epherical.professions.api.event.EventKey;
import com.epherical.professions.api.event.ProfessionEvent;

public abstract class AbstractProfessionEvent implements ProfessionEvent {

    private final EventKey<? extends ProfessionEvent> key;

    protected AbstractProfessionEvent(EventKey<? extends ProfessionEvent> key) {
        this.key = key;
    }

    @Override
    public EventKey<? extends ProfessionEvent> key() {
        return this.key;
    }
}

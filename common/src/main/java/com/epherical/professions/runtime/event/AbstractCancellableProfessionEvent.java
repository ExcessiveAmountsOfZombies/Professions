package com.epherical.professions.runtime.event;

import com.epherical.professions.api.event.CancellableProfessionEvent;
import com.epherical.professions.api.event.EventKey;
import com.epherical.professions.api.event.ProfessionEvent;

public abstract class AbstractCancellableProfessionEvent extends AbstractProfessionEvent implements CancellableProfessionEvent {

    private volatile boolean canceled;

    protected AbstractCancellableProfessionEvent(EventKey<? extends ProfessionEvent> key) {
        super(key);
    }

    @Override
    public boolean isCanceled() {
        return this.canceled;
    }

    @Override
    public void setCanceled(boolean canceled) {
        this.canceled = canceled;
    }
}

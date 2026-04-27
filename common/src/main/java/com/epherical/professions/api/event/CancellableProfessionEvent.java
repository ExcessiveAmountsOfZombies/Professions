package com.epherical.professions.api.event;

/**
 * Optional contract for events that can stop further listener processing.
 * Events that do not implement this remain non-cancelable.
 */
public interface CancellableProfessionEvent extends ProfessionEvent {

    boolean isCanceled();

    void setCanceled(boolean canceled);
}

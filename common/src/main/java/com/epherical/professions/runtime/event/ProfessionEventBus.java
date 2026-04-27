package com.epherical.professions.runtime.event;

import com.epherical.professions.ProfessionsCommon;
import com.epherical.professions.api.event.EventKey;
import com.epherical.professions.api.event.EventListener;
import com.epherical.professions.api.event.EventPhase;
import com.epherical.professions.api.event.CancellableProfessionEvent;
import com.epherical.professions.api.event.ProfessionEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class ProfessionEventBus {
    public static final int FIRST = -10_000;
    public static final int EARLY = -1_000;
    public static final int NORMAL = 0;
    public static final int LATE = 1_000;
    public static final int LAST = 10_000;

    private static final Comparator<RegisteredListener> ORDERING = Comparator
            .comparing(RegisteredListener::phase)
            .thenComparingInt(RegisteredListener::priority)
            .thenComparingLong(RegisteredListener::registrationOrder);

    private final Map<EventKey<?>, ListenerRegistry> listeners = new ConcurrentHashMap<>();
    private final AtomicLong registrationOrder = new AtomicLong();

    public <E extends ProfessionEvent> void register(EventKey<E> key, EventListener<? super E> listener) {
        this.register(key, EventPhase.APPLY, NORMAL, listener);
    }

    public <E extends ProfessionEvent> void register(EventKey<E> key, EventPhase phase, EventListener<? super E> listener) {
        this.register(key, phase, NORMAL, listener);
    }

    public <E extends ProfessionEvent> void register(EventKey<E> key, EventPhase phase, int priority, EventListener<? super E> listener) {
        Objects.requireNonNull(key, "key");
        Objects.requireNonNull(phase, "phase");
        Objects.requireNonNull(listener, "listener");

        ListenerRegistry registrations = this.listeners.computeIfAbsent(key, ignored -> new ListenerRegistry());
        registrations.add(new RegisteredListener(phase, priority, this.registrationOrder.getAndIncrement(), castListener(listener)));
    }

    public <E extends ProfessionEvent> void post(E event) {
        Objects.requireNonNull(event, "event");

        // todo; remove this
        ProfessionsCommon.LOG.info("EventFire: {}", event.getClass().getSimpleName());

        ListenerRegistry registrations = this.listeners.get(event.key());
        if (registrations == null) {
            return;
        }

        RegisteredListener[] snapshot = registrations.snapshot();
        if (snapshot.length == 0) {
            return;
        }

        CancellableProfessionEvent cancellableEvent = event instanceof CancellableProfessionEvent cancellable ? cancellable : null;
        if (cancellableEvent != null && cancellableEvent.isCanceled()) {
            return;
        }

        for (RegisteredListener registration : snapshot) {
            registration.listener().handle(event);
            if (cancellableEvent != null && cancellableEvent.isCanceled()) {
                break;
            }
        }
    }

    @SuppressWarnings("unchecked")
    private static EventListener<ProfessionEvent> castListener(EventListener<?> listener) {
        return (EventListener<ProfessionEvent>) listener;
    }

    private record RegisteredListener(
            EventPhase phase,
            int priority,
            long registrationOrder,
            EventListener<ProfessionEvent> listener
    ) {
    }

    private static final class ListenerRegistry {
        private static final RegisteredListener[] EMPTY = new RegisteredListener[0];

        private final List<RegisteredListener> mutable = new ArrayList<>();
        private volatile RegisteredListener[] snapshot = EMPTY;

        void add(RegisteredListener registration) {
            synchronized (this) {
                int insertAt = Collections.binarySearch(this.mutable, registration, ORDERING);
                if (insertAt < 0) {
                    insertAt = -insertAt - 1;
                }
                this.mutable.add(insertAt, registration);
                this.snapshot = this.mutable.toArray(RegisteredListener[]::new);
            }
        }

        RegisteredListener[] snapshot() {
            return this.snapshot;
        }
    }
}

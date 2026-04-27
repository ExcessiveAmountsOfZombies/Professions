package com.epherical.professions.api.event;

@FunctionalInterface
public interface EventListener<E extends ProfessionEvent> {

    void handle(E event);
}

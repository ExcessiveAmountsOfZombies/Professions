package com.epherical.professions.api;

import com.epherical.professions.data.Storage;
import com.epherical.professions.profession.ProfessionContext;

import java.util.UUID;

public interface ProfessionalPlayer {

    UUID getUuid();

    void handleAction(ProfessionContext context);

    /**
     * Manually save the player using an indicated storage method.
     * @param storage The storage method to use to save the player.
     */
    void save(Storage<ProfessionalPlayer, UUID> storage);

    /**
     * Indicate when changes have taken place on the Player that require saving.
     */
    void needsToBeSaved();
}

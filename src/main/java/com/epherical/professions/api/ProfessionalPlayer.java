package com.epherical.professions.api;

import com.epherical.professions.profession.ProfessionContext;

import java.util.UUID;

public interface ProfessionalPlayer {

    UUID getUuid();

    void handleAction(ProfessionContext context);

}

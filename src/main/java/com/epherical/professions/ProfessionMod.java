package com.epherical.professions;

import com.epherical.professions.api.ProfessionalPlayer;
import com.epherical.professions.data.Storage;
import com.epherical.professions.datapack.ProfessionLoader;

import java.util.UUID;

public abstract class ProfessionMod {

    public abstract ProfessionLoader getProfessionLoader();
    public abstract PlayerManager getPlayerManager();
    public abstract Storage<ProfessionalPlayer, UUID> getDataStorage();
}

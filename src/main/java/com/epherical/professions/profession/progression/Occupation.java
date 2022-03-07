package com.epherical.professions.profession.progression;

import com.epherical.professions.api.ProfessionalPlayer;
import com.epherical.professions.profession.Profession;

public class Occupation {
    private final ProfessionalPlayer player;
    private final Profession profession;
    private double exp;
    private int level;
    private boolean active;

    public Occupation(ProfessionalPlayer player, Profession profession, double exp, int level, boolean active) {
        this.player = player;
        this.profession = profession;
        this.exp = exp;
        this.level = level;
        this.active = active;
    }

    public boolean isActive() {
        return active;
    }

    public Profession getProfession() {
        return profession;
    }
}

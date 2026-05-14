package com.epherical.professions.model.gating;

import com.epherical.professions.core.context.ProfessionContext;
import com.epherical.professions.model.Occupation;

public abstract class LevelGate<T> extends Gate<T> {

    protected int level;

    public LevelGate(int level) {
        this.level = level;
    }

    public int getLevel() {
        return level;
    }

    @Override
    public boolean meetsGateRequirement(Occupation occupation, ProfessionContext context) {
        return (occupation.getLevel() >= level);
    }
}

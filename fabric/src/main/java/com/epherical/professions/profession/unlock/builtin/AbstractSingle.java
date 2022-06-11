package com.epherical.professions.profession.unlock.builtin;

import com.epherical.professions.profession.unlock.Unlock;
import com.epherical.professions.util.Tristate;
import net.minecraft.network.chat.Component;

public abstract class AbstractSingle<T> implements Unlock.Singular<T> {
    protected final T value;
    protected final int level;
    protected final Component professionDisplay;

    public AbstractSingle(T value, int level, Component professionDisplay) {
        this.value = value;
        this.level = level;
        this.professionDisplay = professionDisplay;
    }

    @Override
    public Tristate isLocked(T object, int level) {
        return (level >= this.level && value.equals(object)) ? Tristate.TRUE : Tristate.FALSE;
    }

    @Override
    public T getObject() {
        return value;
    }

    @Override
    public int getUnlockLevel() {
        return level;
    }

    @Override
    public Component getProfessionDisplay() {
        return professionDisplay;
    }
}

package com.epherical.professions.util;

public enum Tristate {
    TRUE(true),
    FALSE(false),
    UNKNOWN(true);

    private final boolean value;

    Tristate(boolean value) {
        this.value = value;
    }

    public boolean valid() {
        return value;
    }
}

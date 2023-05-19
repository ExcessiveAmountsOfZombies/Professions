package com.epherical.professions.profession.operation;

import net.minecraft.resources.ResourceLocation;

public class LevelRequirement {

    private String occupation;
    private int level;


    public LevelRequirement(String occupation, int level) {
        this.occupation = occupation;
        this.level = level;
    }

    public String getOccupation() {
        return occupation;
    }

    public void setOccupation(String occupation) {
        this.occupation = occupation;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public ResourceLocation getOccupationKey() {
        return new ResourceLocation(occupation);
    }
}

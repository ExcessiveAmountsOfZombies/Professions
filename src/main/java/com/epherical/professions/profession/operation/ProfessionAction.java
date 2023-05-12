package com.epherical.professions.profession.operation;

import com.epherical.professions.profession.action.Action;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public class ProfessionAction<T> {

    private List<ResourceLocation> occupations;
    private Action<T> action;

    public ProfessionAction(List<ResourceLocation> occupations, Action<T> action) {
        this.occupations = occupations;
        this.action = action;
    }

    public Action<T> getAction() {
        return action;
    }

    public List<ResourceLocation> getOccupations() {
        return occupations;
    }
}

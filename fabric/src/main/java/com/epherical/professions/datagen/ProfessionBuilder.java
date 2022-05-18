package com.epherical.professions.datagen;

import com.epherical.org.mbertoli.jfep.Parser;
import com.epherical.professions.profession.Profession;
import com.epherical.professions.profession.action.Action;
import com.epherical.professions.profession.action.ActionType;
import com.google.common.collect.LinkedHashMultimap;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceLocation;


public class ProfessionBuilder {
    protected TextColor color;
    protected TextColor descriptionColor;
    protected String[] description;
    protected String displayName;
    protected int maxLevel;

    protected LinkedHashMultimap<ActionType, Action> actions;
    protected Parser experienceScalingEquation;
    protected Parser incomeScalingEquation;

    protected ResourceLocation key;

    public ProfessionBuilder(TextColor color, TextColor descriptionColor, String[] description, String displayName, int maxLevel) {
        this.color = color;
        this.descriptionColor = descriptionColor;
        this.description = description;
        this.displayName = displayName;
        this.maxLevel = maxLevel;
        this.actions = LinkedHashMultimap.create();
    }

    public static ProfessionBuilder profession(TextColor color, TextColor descriptionColor, String[] description, String displayName, int maxLevel) {
        return new ProfessionBuilder(color, descriptionColor, description, displayName, maxLevel);
    }

    public ProfessionBuilder addExperienceScaling(Parser parser) {
        experienceScalingEquation = parser;
        return this;
    }

    public ProfessionBuilder incomeScaling(Parser parser) {
        incomeScalingEquation = parser;
        return this;
    }

    public ProfessionBuilder addAction(ActionType type, Action action) {
        this.actions.put(type, action);
        return this;
    }

    public Profession build() {
        return new Profession(color, descriptionColor, description, displayName, maxLevel, actions.asMap(), experienceScalingEquation, incomeScalingEquation);
    }
}

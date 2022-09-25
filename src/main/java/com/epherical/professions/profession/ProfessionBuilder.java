package com.epherical.professions.profession;

import com.epherical.org.mbertoli.jfep.Parser;
import com.epherical.professions.profession.action.Action;
import com.epherical.professions.profession.action.ActionType;
import com.epherical.professions.profession.modifiers.BasicModifiers;
import com.epherical.professions.profession.modifiers.Modifiers;
import com.epherical.professions.profession.modifiers.milestones.Milestone;
import com.epherical.professions.profession.modifiers.perks.Perk;
import com.epherical.professions.profession.unlock.Unlock;
import com.epherical.professions.profession.unlock.UnlockType;
import com.google.common.collect.LinkedHashMultimap;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceLocation;


public class ProfessionBuilder {
    protected TextColor professionColor;
    protected TextColor descriptionColor;
    protected String[] description;
    protected String displayName;
    protected int maxLevel;

    protected LinkedHashMultimap<ActionType, Action<?>> actions;
    protected LinkedHashMultimap<UnlockType<?>, Unlock<?>> unlocks;
    protected Parser experienceScalingEquation;
    protected Parser incomeScalingEquation;
    protected Modifiers modifiers;

    protected ResourceLocation key;

    private ProfessionBuilder(TextColor professionColor, TextColor descriptionColor, String[] description, String displayName, int maxLevel) {
        this.professionColor = professionColor;
        this.descriptionColor = descriptionColor;
        this.description = description;
        this.displayName = displayName;
        this.maxLevel = maxLevel;
        this.actions = LinkedHashMultimap.create();
        this.unlocks = LinkedHashMultimap.create();
        this.modifiers = new BasicModifiers(new Milestone[0], new Perk[0]);
    }

    public static ProfessionBuilder profession(TextColor professionColor, TextColor descriptionColor, String[] description, String displayName, int maxLevel) {
        return new ProfessionBuilder(professionColor, descriptionColor, description, displayName, maxLevel);
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

    public ProfessionBuilder addAction(ActionType type, Action.Builder action) {
        this.actions.put(type, action.build());
        return this;
    }

    public ProfessionBuilder addUnlock(UnlockType<?> type, Unlock<?> unlock) {
        this.unlocks.put(type, unlock);
        return this;
    }

    public ProfessionBuilder addUnlock(UnlockType<?> type, Unlock.Builder<?> unlock) {
        this.unlocks.put(type, unlock.build());
        return this;
    }

    public ProfessionBuilder setProfessionColor(TextColor professionColor) {
        this.professionColor = professionColor;
        return this;
    }

    public ProfessionBuilder setActions(LinkedHashMultimap<ActionType, Action<?>> actions) {
        this.actions = actions;
        return this;
    }

    public ProfessionBuilder setDescription(String[] description) {
        this.description = description;
        return this;
    }

    public ProfessionBuilder setDescriptionColor(TextColor descriptionColor) {
        this.descriptionColor = descriptionColor;
        return this;
    }

    public ProfessionBuilder setDisplayName(String displayName) {
        this.displayName = displayName;
        return this;
    }

    public ProfessionBuilder setExperienceScalingEquation(Parser experienceScalingEquation) {
        this.experienceScalingEquation = experienceScalingEquation;
        return this;
    }

    public ProfessionBuilder setIncomeScalingEquation(Parser incomeScalingEquation) {
        this.incomeScalingEquation = incomeScalingEquation;
        return this;
    }

    public ProfessionBuilder setMaxLevel(int maxLevel) {
        this.maxLevel = maxLevel;
        return this;
    }

    public ProfessionBuilder setModifiers(Modifiers modifiers) {
        this.modifiers = modifiers;
        return this;
    }

    public void setKey(ResourceLocation key) {
        this.key = key;
    }

    public Profession build() {
        return new Profession(professionColor, descriptionColor, description, displayName, maxLevel,
                actions.asMap(), unlocks.asMap(), experienceScalingEquation, incomeScalingEquation, key, modifiers);
    }
}

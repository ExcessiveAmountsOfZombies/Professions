package com.epherical.professions.profession;

import com.epherical.org.mbertoli.jfep.Parser;
import com.epherical.professions.profession.action.Action;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;

import java.lang.reflect.Type;
import java.util.Objects;

public class Profession {
    private final TextColor color;
    private final TextColor descriptionColor;
    private final String[] description;
    private final String displayName;
    private final int maxLevel;
    private final Action[] actions;
    private final Parser experienceScalingEquation;
    private final Parser incomeScalingEquation;

    private ResourceLocation key;

    public Profession(TextColor color, TextColor descriptionColor, String[] description, String displayName, int maxLevel, Action[] actions,
                      Parser experienceScalingEquation, Parser incomeScalingEquation) {
        this.color = color;
        this.description = description;
        this.descriptionColor = descriptionColor;
        this.displayName = displayName;
        this.maxLevel = maxLevel;
        this.actions = actions;
        this.experienceScalingEquation = experienceScalingEquation;
        this.incomeScalingEquation = incomeScalingEquation;
    }

    public int getMaxLevel() {
        return maxLevel;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String[] getDescription() {
        return description;
    }

    public TextColor getDescriptionColor() {
        return descriptionColor;
    }

    public TextColor getColor() {
        return color;
    }

    public Action[] getActions() {
        return actions;
    }

    public double getExperienceForLevel(int level) {
        experienceScalingEquation.setVariable("lvl", level);
        return experienceScalingEquation.getValue();
    }

    public double getIncomeForLevel(double income) {
        incomeScalingEquation.setVariable("base", income);
        return incomeScalingEquation.getValue();
    }

    public boolean isSameProfession(Profession profession) {
        return Objects.equals(this, profession);
    }

    public void setKey(ResourceLocation key) {
        this.key = key;
    }

    public ResourceLocation getKey() {
        return key;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Profession that = (Profession) o;
        return key.equals(that.key);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key);
    }

    public static class Serializer implements ProfessionSerializer<Profession> {


        @Override
        public Profession deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject object = GsonHelper.convertToJsonObject(json, "profession object");
            TextColor professionColor = TextColor.parseColor(GsonHelper.getAsString(object, "color", "#FF0000"));
            TextColor descriptionColor = TextColor.parseColor(GsonHelper.getAsString(object, "descriptionColor", "#FFFFFF"));
            String[] description = GsonHelper.getAsObject(object, "description", context, String[].class);
            String displayName = GsonHelper.getAsString(object, "displayName");
            int maxLevel = GsonHelper.getAsInt(object, "maxLevel");
            Action[] actions = GsonHelper.getAsObject(object, "actions", new Action[0], context, Action[].class);
            Parser experienceScaling = new Parser(GsonHelper.getAsString(object, "experienceSclEquation"));
            Parser incomeScaling = new Parser(GsonHelper.getAsString(object, "incomeSclEquation"));
            return new Profession(professionColor, descriptionColor, description, displayName, maxLevel, actions, experienceScaling, incomeScaling);
        }

        @Override
        public JsonElement serialize(Profession src, Type typeOfSrc, JsonSerializationContext context) {
            return null;
        }

        @Override
        public Class<Profession> getType() {
            return Profession.class;
        }
    }
}

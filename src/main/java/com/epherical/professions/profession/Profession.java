package com.epherical.professions.profession;

import com.epherical.professions.profession.action.Action;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import net.minecraft.network.chat.TextColor;
import net.minecraft.util.GsonHelper;

import java.lang.reflect.Type;

public class Profession {
    private final TextColor color;
    private final TextColor descriptionColor;
    private final String[] description;
    private final String displayName;
    private final int maxLevel;
    private final Action[] actions;

    public Profession(TextColor color, TextColor descriptionColor, String[] description, String displayName, int maxLevel, Action[] actions) {
        this.color = color;
        this.description = description;
        this.descriptionColor = descriptionColor;
        this.displayName = displayName;
        this.maxLevel = maxLevel;
        this.actions = actions;
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
            return new Profession(professionColor, descriptionColor, description, displayName, maxLevel, actions);
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

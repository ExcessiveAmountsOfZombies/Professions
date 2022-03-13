package com.epherical.professions.profession.progression;

import com.epherical.professions.ProfessionsMod;
import com.epherical.professions.profession.Profession;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;

import java.lang.reflect.Type;

public class Occupation {
    private final Profession profession;
    private double exp;
    private int level;
    private boolean active;

    public Occupation(Profession profession, double exp, int level, boolean active) {
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


    public static class Serializer implements JsonDeserializer<Occupation>, JsonSerializer<Occupation> {


        @Override
        public Occupation deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject object = json.getAsJsonObject();
            Profession profession = ProfessionsMod.professionLoader.getProfession(new ResourceLocation(GsonHelper.getAsString(object, "prof")));
            double exp = GsonHelper.getAsInt(object, "exp");
            int level = GsonHelper.getAsInt(object, "lvl");
            boolean active = GsonHelper.getAsBoolean(object, "active");
            return new Occupation(profession, exp, level, active);
        }

        @Override
        public JsonElement serialize(Occupation src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject object = new JsonObject();
            object.addProperty("exp", src.exp);
            object.addProperty("lvl", src.level);
            object.addProperty("active", src.active);
            object.addProperty("prof", ProfessionsMod.professionLoader.getIDFromProfession(src.profession).toString());
            return object;
        }
    }
}

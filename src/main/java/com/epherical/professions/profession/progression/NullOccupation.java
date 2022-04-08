package com.epherical.professions.profession.progression;

import com.epherical.professions.profession.Profession;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

public class NullOccupation extends Occupation {
    private final String professionID;

    public NullOccupation(String id, double exp, int level, OccupationSlot slot) {
        super(null, exp, level, slot);
        this.professionID = id;
    }

    @Override
    public boolean isActive() {
        return false;
    }

    @Override
    public boolean isProfession(Profession profession) {
        return false;
    }

    @Override
    public void resetMaxExperience() {}

    @Override
    public boolean checkIfLevelUp() {
        return false;
    }

    public static class Serializer implements JsonSerializer<NullOccupation> {


        @Override
        public JsonElement serialize(NullOccupation src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject object = new JsonObject();
            object.addProperty("exp", src.getExp());
            object.addProperty("lvl", src.getLevel());
            object.addProperty("slot", src.getSlotStatus().name());
            object.addProperty("prof", src.professionID);
            return object;
        }
    }

}

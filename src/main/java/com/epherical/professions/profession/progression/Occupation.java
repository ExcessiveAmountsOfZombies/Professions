package com.epherical.professions.profession.progression;

import com.epherical.professions.ProfessionsMod;
import com.epherical.professions.api.ProfessionalPlayer;
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
import java.util.Locale;
import java.util.Objects;

public class Occupation {
    private final Profession profession;
    private double exp;
    private int level;
    private OccupationSlot slot;
    private transient int maxExp = -1;

    public Occupation(Profession profession, double exp, int level, OccupationSlot slot) {
        this.profession = profession;
        this.exp = exp;
        this.level = level;
        this.slot = slot;
    }

    /**
     * CLIENT CONSTRUCTOR ONLY
     */
    public Occupation(Profession profession, double exp, int maxExp, int level) {
        this.profession = profession;
        this.exp = exp;
        this.maxExp = maxExp;
        this.level = level;
        this.slot = OccupationSlot.ACTIVE;
    }

    public boolean isActive() {
        return slot != OccupationSlot.INACTIVE;
    }

    public OccupationSlot getSlotStatus() {
        return slot;
    }

    public void setSlot(OccupationSlot slot) {
        this.slot = slot;
    }

    public Profession getProfession() {
        return profession;
    }

    public boolean addExp(double exp, ProfessionalPlayer player) {
        player.needsToBeSaved();
        this.exp += exp;
        return checkIfLevelUp();
    }

    public void setLevel(int level, ProfessionalPlayer player) {
        player.needsToBeSaved();
        this.level = level;
        this.exp = 0;
        resetMaxExperience();
        checkIfLevelUp();
    }

    public boolean checkIfLevelUp() {
        boolean willLevel = false;

        while (exp >= maxExp) {
            if (profession.getMaxLevel() > 0 && level >= profession.getMaxLevel()) {
                break;
            }
            level++;
            exp -= maxExp;
            willLevel = true;
            resetMaxExperience();
        }

        if (exp > maxExp) {
            exp = maxExp;
        }

        return willLevel;
    }

    public double getExp() {
        return exp;
    }

    public int getMaxExp() {
        return maxExp;
    }

    public int getLevel() {
        return level;
    }

    public void resetMaxExperience() {
        this.maxExp = (int) profession.getExperienceForLevel(level);
    }

    public boolean isProfession(Profession profession) {
        return this.profession.isSameProfession(profession);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Occupation that = (Occupation) o;
        return Double.compare(that.exp, exp) == 0 && level == that.level && profession.equals(that.profession);
    }

    @Override
    public int hashCode() {
        return Objects.hash(profession, exp, level);
    }

    public static class Serializer implements JsonDeserializer<Occupation>, JsonSerializer<Occupation> {


        @Override
        public Occupation deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject object = json.getAsJsonObject();
            Profession profession = ProfessionsMod.getInstance().getProfessionLoader().getProfession(new ResourceLocation(GsonHelper.getAsString(object, "prof")));
            double exp = GsonHelper.getAsInt(object, "exp");
            int level = GsonHelper.getAsInt(object, "lvl");
            OccupationSlot active = OccupationSlot.valueOf(GsonHelper.getAsString(object, "slot").toUpperCase(Locale.ROOT));
            return new Occupation(profession, exp, level, active);
        }

        @Override
        public JsonElement serialize(Occupation src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject object = new JsonObject();
            object.addProperty("exp", src.exp);
            object.addProperty("lvl", src.level);
            object.addProperty("slot", src.slot.name());
            object.addProperty("prof", ProfessionsMod.getInstance().getProfessionLoader().getIDFromProfession(src.profession).toString());
            return object;
        }
    }
}

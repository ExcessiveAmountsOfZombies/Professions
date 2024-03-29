package com.epherical.professions.profession.progression;

import com.epherical.professions.ProfessionPlatform;
import com.epherical.professions.api.CachedData;
import com.epherical.professions.api.ProfessionalPlayer;
import com.epherical.professions.profession.CachedDataImpl;
import com.epherical.professions.profession.Profession;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.mojang.logging.LogUtils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import org.slf4j.Logger;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class Occupation {
    private static final Logger LOGGER = LogUtils.getLogger();
    private final Profession profession;
    private final CachedData data;
    private double exp;
    private int level;
    private int receivedBenefitsUpToLevel;
    private OccupationSlot slot;
    private transient int maxExp = -1;

    public Occupation(Profession profession, double exp, int level, OccupationSlot slot) {
        this.profession = profession;
        this.exp = exp;
        this.level = level;
        this.receivedBenefitsUpToLevel = level;
        this.slot = slot;
        this.data = new CachedDataImpl(this);
    }

    /**
     * CLIENT CONSTRUCTOR ONLY
     */
    public Occupation(Profession profession, double exp, int maxExp, int level) {
        this.profession = profession;
        this.exp = exp;
        this.maxExp = maxExp;
        this.level = level;
        this.receivedBenefitsUpToLevel = level;
        this.slot = OccupationSlot.ACTIVE;
        this.data = new CachedDataImpl(this);
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
        return checkIfLevelUp(player);
    }

    public void setLevel(int level, ProfessionalPlayer player) {
        player.needsToBeSaved();
        this.level = level;
        // todo; add a giveMilestones parameter
        profession.getBenefits().handleLevelUp(player, this);
        this.exp = 0;
        resetMaxExperience();
        checkIfLevelUp(player);
    }

    public boolean checkIfLevelUp(ProfessionalPlayer player) {
        boolean willLevel = false;

        while (exp >= maxExp) {
            if (profession.getMaxLevel() > 0 && level >= profession.getMaxLevel()) {
                break;
            }
            level++;
            profession.getBenefits().handleLevelUp(player, this);
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

    public int getReceivedBenefitsUpToLevel() {
        return receivedBenefitsUpToLevel;
    }

    public void setReceivedBenefitsUpToLevel(int receivedBenefitsUpToLevel) {
        this.receivedBenefitsUpToLevel = receivedBenefitsUpToLevel;
    }

    public CachedData getData() {
        return data;
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

    public static void toNetwork(FriendlyByteBuf buf, List<Occupation> occupations, boolean sendUnlocks) {
        buf.writeVarInt(occupations.size());
        for (Occupation occupation : occupations) {
            Profession.toNetwork(buf, occupation.getProfession(), sendUnlocks);
            buf.writeVarInt(occupation.getLevel());
            buf.writeDouble(occupation.getExp());
            buf.writeVarInt(occupation.getMaxExp());
        }
    }

    public static class Serializer implements JsonDeserializer<Occupation>, JsonSerializer<Occupation> {


        @Override
        public Occupation deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject object = json.getAsJsonObject();
            String id = GsonHelper.getAsString(object, "prof");
            Profession profession = ProfessionPlatform.platform.getProfessionLoader().getProfession(new ResourceLocation(id));
            double exp = GsonHelper.getAsInt(object, "exp");
            int level = GsonHelper.getAsInt(object, "lvl");
            OccupationSlot active = OccupationSlot.valueOf(GsonHelper.getAsString(object, "slot").toUpperCase(Locale.ROOT));
            if (profession == null) {
                LOGGER.warn("Could not find profession {}. Will load the profession but it can't be interacted with.", id);
                return new NullOccupation(GsonHelper.getAsString(object, "prof"), exp, level, active);
            }
            return new Occupation(profession, exp, level, active);
        }

        @Override
        public JsonElement serialize(Occupation src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject object = new JsonObject();
            object.addProperty("exp", src.exp);
            object.addProperty("lvl", src.level);
            object.addProperty("slot", src.slot.name());
            object.addProperty("prof", ProfessionPlatform.platform.getProfessionLoader().getIDFromProfession(src.profession).toString());
            return object;
        }
    }
}

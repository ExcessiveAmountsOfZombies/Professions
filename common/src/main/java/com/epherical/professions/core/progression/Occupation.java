package com.epherical.professions.core.progression;

import com.epherical.professions.CommonClass;
import com.epherical.professions.api.IProfessionalPlayer;
import com.epherical.professions.core.Profession;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.resources.RegistryFixedCodec;
import org.slf4j.Logger;

import java.util.Locale;
import java.util.Objects;

public class Occupation {

    public static final Codec<OccupationSlot> SLOT_CODEC =
            Codec.STRING.xmap(s -> OccupationSlot.valueOf(s.toUpperCase(Locale.ROOT)),
                    OccupationSlot::name);

    public static final Codec<Occupation> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            RegistryFixedCodec.create(CommonClass.PROFESSION_REGISTRY_KEY).fieldOf("occupation").forGetter(o -> o.profession),
            Codec.DOUBLE.fieldOf("exp").forGetter(o -> o.exp),
            Codec.INT.fieldOf("level").forGetter(o -> o.level),
            SLOT_CODEC.fieldOf("slot").forGetter(o -> o.slot)
    ).apply(instance, Occupation::new));


    private static final Logger LOGGER = LogUtils.getLogger();
    private final Holder<Profession> profession;
    //private final CachedData data;
    private double exp;
    private int level;
    private int receivedBenefitsUpToLevel;
    private OccupationSlot slot;
    private transient int maxExp = -1;

    public Occupation(Holder<Profession> profession, double exp, int level, OccupationSlot slot) {
        this.profession = profession;
        this.exp = exp;
        this.level = level;
        this.receivedBenefitsUpToLevel = level;
        this.slot = slot;
        //this.data = new CachedDataImpl(this);

    }

    /**
     * CLIENT CONSTRUCTOR ONLY
     */
    /*public Occupation(Profession profession, double exp, int maxExp, int level) {
        this.profession = profession;
        this.exp = exp;
        this.maxExp = maxExp;
        this.level = level;
        this.receivedBenefitsUpToLevel = level;
        this.slot = OccupationSlot.ACTIVE;
        this.data = new CachedDataImpl(this);
    }*/

    public boolean isActive() {
        return slot != OccupationSlot.INACTIVE;
    }

    public OccupationSlot getSlotStatus() {
        return slot;
    }

    public void setSlot(OccupationSlot slot) {
        this.slot = slot;
    }

    public boolean addExp(double exp, IProfessionalPlayer player) {
        //player.needsToBeSaved();
        this.exp += exp;
        return checkIfLevelUp(player);
    }

    public void setLevel(int level, IProfessionalPlayer player) {
        //player.needsToBeSaved();
        this.level = level;
        // todo; add a giveMilestones parameter
       // profession.getBenefits().handleLevelUp(player, this);
        this.exp = 0;
        resetMaxExperience();
        checkIfLevelUp(player);
    }

    public boolean checkIfLevelUp(IProfessionalPlayer player) {
        /*boolean willLevel = false;

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

        return willLevel;*/
        return false;
    }


    public void setReceivedBenefitsUpToLevel(int receivedBenefitsUpToLevel) {
        this.receivedBenefitsUpToLevel = receivedBenefitsUpToLevel;
    }

    public void resetMaxExperience() {
       // this.maxExp = (int) profession.getExperienceForLevel(level);
    }

    public boolean isProfession(Profession profession) {
        return false;
       // return this.profession.isSameProfession(profession);
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
    public Holder<Profession> getProfession() {
        return profession;
    }

    public double getExp() {
        return exp;
    }

    public int getLevel() {
        return level;
    }

    public int getReceivedBenefitsUpToLevel() {
        return receivedBenefitsUpToLevel;
    }

    public OccupationSlot getSlot() {
        return slot;
    }

    public int getMaxExp() {
        return maxExp;
    }
}

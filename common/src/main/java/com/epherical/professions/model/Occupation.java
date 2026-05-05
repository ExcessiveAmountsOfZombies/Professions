package com.epherical.professions.model;

import com.epherical.professions.api.IProfessionalPlayer;
import com.epherical.professions.core.Profession;
import com.epherical.professions.core.progression.OccupationSlot;
import com.epherical.professions.domain.exception.ProfessionNotActiveException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.math.BigDecimal;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

import static com.epherical.professions.ProfessionsCommon.PROFESSION_REGISTRY_KEY;

public class Occupation {

    public static final Codec<OccupationSlot> SLOT_CODEC =
            Codec.STRING.xmap(s -> OccupationSlot.valueOf(s.toUpperCase(Locale.ROOT)),
                    OccupationSlot::name);

    public static final Codec<Occupation> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ResourceLocation.CODEC.fieldOf("profession").forGetter(Occupation::getProfessionKey),
            ExperienceData.CODEC.fieldOf("experience").forGetter(Occupation::getExperience),
            SLOT_CODEC.fieldOf("slot").forGetter(Occupation::getSlot)
    ).apply(instance, Occupation::new));

    private final ResourceLocation professionKey;
    private final ExperienceData experience;
    private int receivedBenefitsUpToLevel;
    private OccupationSlot slot;


    private boolean professionExists;
    private transient Holder<Profession> profession;
    private transient BigDecimal maxLevelExp = BigDecimal.valueOf(-1);

    public Occupation(ResourceLocation professionKey, ExperienceData experience, OccupationSlot slot) {
        this.professionKey = professionKey;
        this.experience = experience;
        this.receivedBenefitsUpToLevel = experience.level;
        this.slot = slot;
        this.professionExists = false;
        // rest is called in resolveProfession
    }

    public Occupation(Holder<Profession> profession, double exp, int level, OccupationSlot slot) {
        this(profession.unwrapKey().orElseThrow().location(), new ExperienceData(exp, level, exp), slot);
        this.profession = profession;
        this.professionExists = true;
        resetMaxExperience();
        synchronizeProgressionState();
    }

    public void resolveProfession(@Nullable RegistryAccess registryAccess) {
        this.profession = null;
        this.professionExists = false;
        this.maxLevelExp = BigDecimal.valueOf(-1);

        if (registryAccess == null) {
            return;
        }

        registryAccess.lookup(PROFESSION_REGISTRY_KEY)
                .flatMap(lookup -> lookup.get(ResourceKey.create(PROFESSION_REGISTRY_KEY, professionKey)))
                .ifPresent(holder -> {
                    this.profession = holder;
                    this.professionExists = true;
                    resetMaxExperience();
                    synchronizeProgressionState();
                });
    }

    public boolean addExp(double exp, IProfessionalPlayer player) throws ProfessionNotActiveException {
        if (!professionExists) {
            throw new ProfessionNotActiveException("Profession not active! " + professionKey);
        }
        player.markDirty(true);

        BigDecimal delta = BigDecimal.valueOf(exp);
        this.experience.expProgress = this.experience.expProgress.add(delta);
        this.experience.expTotal = this.experience.expTotal.add(delta);
        return checkIfLevelUp(player);
    }

    public void setLevel(int level, IProfessionalPlayer player) throws ProfessionNotActiveException {
        if (!professionExists) {
            throw new ProfessionNotActiveException("Profession not active! " + professionKey);
        }

        player.markDirty(true);
        this.experience.level = level;
        this.experience.expProgress = BigDecimal.ZERO;

        BigDecimal total = BigDecimal.ZERO;
        for (int i = 0; i < level; i++) {
            total = total.add(BigDecimal.valueOf(getProfession().value().getExperienceForLevel(i)));
        }
        this.experience.expTotal = total;

        resetMaxExperience();
        this.experience.progressionSignature = getProfession().value().getProgressionSignature();
    }

    public boolean checkIfLevelUp(IProfessionalPlayer player) throws ProfessionNotActiveException {
        if (!professionExists || profession == null) {
            throw new ProfessionNotActiveException("Profession not active! " + professionKey);
        }

        boolean willLevel = false;

        while (experience.expProgress.compareTo(maxLevelExp) >= 0) {
            if (profession.value().maxLevel() > 0 && experience.level >= profession.value().maxLevel()) {
                break;
            }


            experience.level++;
            experience.expProgress = experience.expProgress.subtract(maxLevelExp);
            willLevel = true;
            resetMaxExperience();
        }

        if (experience.expProgress.compareTo(maxLevelExp) > 0) {
            experience.expProgress = maxLevelExp;
        }

        return willLevel;
    }

    public boolean synchronizeProgressionState() {
        if (!professionExists || profession == null) {
            return false;
        }

        String currentSignature = profession.value().getProgressionSignature();
        boolean shouldRecalculate = !Objects.equals(experience.progressionSignature, currentSignature);
        if (shouldRecalculate) {
            recalculateFromTotal();
        } else if (maxLevelExp.signum() < 0) {
            resetMaxExperience();
        }

        experience.progressionSignature = currentSignature;
        return shouldRecalculate;
    }

    private void recalculateFromTotal() {
        double remainingExp = experience.expTotal.doubleValue();
        int resolvedLevel = 0;
        int maxLevel = profession.value().maxLevel();

        // TODO: If formula-driven recalculations ever need to handle very large level jumps efficiently,
        //  replace this direct level-by-level walk with a sparse checkpoint index keyed by the profession's
        //  progression signature. Store cumulative XP totals every N levels (for example 500 or 1000),
        //  grow those checkpoints lazily, then use exponential search + binary search against cumulative
        //  totals to resolve the level. That keeps memory small while avoiding O(level delta) recalculations
        //  when a progression formula change moves players by hundreds of thousands or millions of levels.
        while (remainingExp > 0) {
            if (maxLevel > 0 && resolvedLevel >= maxLevel) {
                break;
            }

            double levelRequirement = profession.value().getExperienceForLevel(resolvedLevel);
            if (remainingExp < levelRequirement) {
                break;
            }

            remainingExp -= levelRequirement;
            resolvedLevel++;
        }

        experience.level = resolvedLevel;
        experience.expProgress = BigDecimal.valueOf(Math.max(0D, remainingExp));
        resetMaxExperience();

        if (experience.expProgress.compareTo(maxLevelExp) > 0) {
            experience.expProgress = maxLevelExp;
        }
    }

    public void setReceivedBenefitsUpToLevel(int receivedBenefitsUpToLevel) {
        this.receivedBenefitsUpToLevel = receivedBenefitsUpToLevel;
    }

    public void resetMaxExperience() {
        if (!professionExists) {
            return;
        }
        this.maxLevelExp = BigDecimal.valueOf(getProfession().value().getExperienceForLevel(this.getLevel()));
    }

    public boolean isProfession(Holder<Profession> profession) {
        return profession.is(this.professionKey);
    }


    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        Occupation that = (Occupation) o;
        return receivedBenefitsUpToLevel == that.receivedBenefitsUpToLevel &&
                professionExists == that.professionExists &&
                Objects.equals(professionKey, that.professionKey) &&
                Objects.equals(experience, that.experience) &&
                slot == that.slot &&
                Objects.equals(profession, that.profession) &&
                Objects.equals(maxLevelExp, that.maxLevelExp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(professionKey, professionExists, experience, receivedBenefitsUpToLevel, slot, maxLevelExp);
    }

    public Holder<Profession> getProfession() {
        if (profession == null) {
            throw new IllegalStateException("Profession " + professionKey + " is unavailable");
        }
        return profession;
    }

    public boolean isActive() {
        return professionExists && slot != OccupationSlot.INACTIVE;
    }

    public OccupationSlot getSlotStatus() {
        return slot;
    }

    public void setSlot(OccupationSlot slot) {
        this.slot = slot;
    }

    public Optional<Holder<Profession>> getProfessionHolder() {
        return Optional.ofNullable(profession);
    }

    public ResourceLocation getProfessionKey() {
        return professionKey;
    }

    public boolean isProfessionExists() {
        return professionExists;
    }

    public void setProfessionExists(boolean professionExists) {
        this.professionExists = professionExists;
    }

    private ExperienceData getExperience() {
        return experience;
    }

    public double getExpProgress() {
        return experience.expProgress.doubleValue();
    }

    public int getLevel() {
        return experience.level;
    }

    public double getLifetimeExp() {
        return experience.expTotal.doubleValue();
    }

    public int getReceivedBenefitsUpToLevel() {
        return receivedBenefitsUpToLevel;
    }

    public OccupationSlot getSlot() {
        return slot;
    }

    public double getMaxExperience() {
        return maxLevelExp.doubleValue();
    }

    public static class ExperienceData {
        public static final Codec<ExperienceData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.DOUBLE.fieldOf("exp").forGetter(experienceData -> experienceData.expProgress.doubleValue()),
                Codec.INT.fieldOf("level").forGetter(ExperienceData::getLevel),
                Codec.DOUBLE.fieldOf("expTotal").forGetter(experienceData -> experienceData.expTotal.doubleValue()),
                Codec.STRING.optionalFieldOf("progressionSignature", "").forGetter(ExperienceData::getProgressionSignature)
        ).apply(instance, ExperienceData::new));

        private BigDecimal expProgress = BigDecimal.ZERO;
        private int level;
        private BigDecimal expTotal = BigDecimal.ZERO;
        private String progressionSignature = "";

        public ExperienceData(double exp, int level, double expTotal) {
            this(exp, level, expTotal, "");
        }

        public ExperienceData(double exp, int level, double expTotal, String progressionSignature) {
            this.expProgress = BigDecimal.valueOf(exp);
            this.level = level;
            this.expTotal = BigDecimal.valueOf(expTotal);
            this.progressionSignature = progressionSignature;
        }

        public BigDecimal getExpProgress() {
            return expProgress;
        }

        public int getLevel() {
            return level;
        }

        public BigDecimal getExpTotal() {
            return expTotal;
        }

        public String getProgressionSignature() {
            return progressionSignature;
        }

        @Override
        public boolean equals(Object o) {
            if (o == null || getClass() != o.getClass()) return false;

            ExperienceData that = (ExperienceData) o;
            return level == that.level && Objects.equals(expProgress, that.expProgress) && Objects.equals(expTotal, that.expTotal);
        }

        @Override
        public int hashCode() {
            return Objects.hash(expProgress, level, expTotal);
        }
    }
}


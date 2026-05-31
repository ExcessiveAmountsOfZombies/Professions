package com.epherical.professions.model;

import com.epherical.professions.ProfessionsCommon;
import com.epherical.professions.api.IProfessionalPlayer;
import com.epherical.professions.core.Profession;
import com.epherical.professions.core.ProfessionCategory;
import com.epherical.professions.core.progression.OccupationSlot;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static com.epherical.professions.ProfessionsCommon.PROFESSION_REGISTRY_KEY;

public class ProfessionalPlayer implements IProfessionalPlayer {

    private static final ResourceLocation MAX_HEALTH_ADDITIVE_MODIFIER_ID = ResourceLocation.fromNamespaceAndPath(ProfessionsCommon.MOD_ID, "perk/max_health/additive");
    private static final ResourceLocation MAX_HEALTH_MULTIPLICATIVE_MODIFIER_ID = ResourceLocation.fromNamespaceAndPath(ProfessionsCommon.MOD_ID, "perk/max_health/multiplicative");
    private static final ResourceLocation ATTACK_DAMAGE_ADDITIVE_MODIFIER_ID = ResourceLocation.fromNamespaceAndPath(ProfessionsCommon.MOD_ID, "perk/attack_damage/additive");
    private static final ResourceLocation ATTACK_DAMAGE_MULTIPLICATIVE_MODIFIER_ID = ResourceLocation.fromNamespaceAndPath(ProfessionsCommon.MOD_ID, "perk/attack_damage/multiplicative");

    private final Map<ResourceLocation, Occupation> occupationMap = new HashMap<>();

    @Nullable
    private ProfessionCategory professionCategory;

    private volatile boolean dirty = false;

    private volatile Player player;
    private UUID uuid;


    public ProfessionalPlayer(UUID uuid, List<Occupation> occupations) {
        this.uuid = uuid;
        for (Occupation occupation : occupations) {
            occupationMap.put(occupation.getProfessionKey(), occupation);
        }
    }

    public ProfessionalPlayer(List<Occupation> occupations, ServerPlayer player, RegistryAccess access) {
        this.player = player;
        this.uuid = player.getUUID();


        for (Occupation occupation : occupations) {
            occupationMap.put(occupation.getProfessionKey(), occupation);
        }

        access.lookupOrThrow(PROFESSION_REGISTRY_KEY).listElements().forEach(element -> {
            Occupation occupation = new Occupation(element, 0,0, OccupationSlot.ACTIVE);
            occupation.resetMaxExperience();
            occupationMap.putIfAbsent(occupation.getProfessionKey(), occupation);
        });

    }




    @Override
    public UUID getUUID() {
        return uuid;
    }

    @Override
    public @Nullable Player getPlayer() {
        return player;
    }

    @Override
    public void setPlayer(@Nullable Player player) {
        this.player = player;
    }

    @Override
    public void markDirty(boolean dirty) {
        this.dirty = dirty;
    }

    @Override
    public void setCategory(@Nullable ProfessionCategory category) {
        this.professionCategory = category;
        this.dirty = true;
    }

    @Override
    public @Nullable ProfessionCategory getCategory() {
        return professionCategory;
    }

    @Override
    public boolean isDirty() {
        return dirty;
    }

    @Override
    public boolean alreadyHasOccupation(Holder<Profession> profession) {
        return false;
    }

    @Override
    public boolean isOccupationActive(Holder<Profession> profession) {
        return false;
    }

    @Override
    public Occupation getOccupation(Holder<Profession> profession) {
        return getOccupation(profession.unwrapKey().get().location());
    }

    @Override
    public Occupation getOccupation(ResourceLocation profession) {
        return occupationMap.get(profession);
    }


    @Override
    public List<Occupation> getAllOccupations() {
        return List.copyOf(occupationMap.values());
    }

    @Override
    public List<Occupation> getActiveOccupations() {
        List<Occupation> activeOccupations = new ArrayList<>();
        if (professionCategory == null) {
            return activeOccupations;
        }
        for (Occupation value : occupationMap.values()) {
            if (professionCategory.hasProfession(value.getProfession())) {
                activeOccupations.add(value);
            }
        }
        return activeOccupations;
    }

    @Override
    public boolean hasClaimedPerk(ResourceLocation perkId) {
        if (perkId == null) {
            return false;
        }

        for (Occupation activeOccupation : getActiveOccupations()) {
            if (activeOccupation.hasClaimedPerk(perkId)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public Set<ResourceLocation> getClaimedPerks() {
        Set<ResourceLocation> claimedPerks = new HashSet<>();
        for (Occupation activeOccupation : getActiveOccupations()) {
            claimedPerks.addAll(activeOccupation.getClaimedPerks());
        }

        return claimedPerks;
    }


    @Override
    public Set<ResourceLocation> getUnlockedPerks() {
        Set<ResourceLocation> unclaimedPerks = new HashSet<>();
        for (Occupation activeOccupation : getActiveOccupations()) {
            unclaimedPerks.addAll(activeOccupation.getUnclaimedPerks());
        }
        return unclaimedPerks;
    }
}

package com.epherical.professions;

import com.epherical.professions.api.IProfessionalPlayer;
import com.epherical.professions.api.event.runtime.PlayerJoinEvent;
import com.epherical.professions.core.Profession;
import com.epherical.professions.model.Occupation;
import com.epherical.professions.model.perks.IStartupPerk;
import com.epherical.professions.model.perks.Perk;
import com.epherical.professions.model.perks.PerkType;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class PerkManager {

    private static final Logger LOGGER = LogManager.getLogger();

    private volatile Map<ResourceLocation, Perk> perksById = Map.of();
    private volatile Map<ResourceLocation, NavigableMap<Integer, List<Perk>>> perksByProfessionAndLevel = Map.of();
    private volatile Map<PerkType, List<Perk>> perksByType = Map.of();

    public void reloadPerks(List<Perk> perks) {
        Map<ResourceLocation, Perk> nextPerksById = new LinkedHashMap<>();
        Map<ResourceLocation, NavigableMap<Integer, List<Perk>>> nextPerksByProfessionAndLevel = new HashMap<>();
        Map<PerkType, List<Perk>> nextPerksByType = new HashMap<>();


        for (Perk perk : perks) {
            ResourceLocation id = perk.getId();
            if (id == null) {
                LOGGER.error("Skipping perk without resolved id: {}", perk);
                continue;
            }

            Perk previous = nextPerksById.put(id, perk);
            if (previous != null) {
                removeOverriddenValueFromNavigableMap(nextPerksByProfessionAndLevel, previous);
                removeFromPerkTypeMap(nextPerksByType, previous);
                LOGGER.debug("Overrode perk {} from datapack merge/override order", id);
            }

            addToNavigableMap(nextPerksByProfessionAndLevel, perk);
            addToTypeMap(nextPerksByType, perk);
        }

        perksById = Map.copyOf(nextPerksById);
        perksByProfessionAndLevel = Map.copyOf(nextPerksByProfessionAndLevel);
        perksByType = Map.copyOf(nextPerksByType);

        LOGGER.info("Reloaded {} perks", nextPerksById.size());
    }

    public Collection<Perk> getPerks() {
        return perksById.values();
    }

    @Nullable
    public Perk getPerk(ResourceLocation id) {
        return perksById.get(id);
    }

    public List<Perk> getPerksByProfession(Holder<Profession> profession) {
        return profession.unwrapKey()
                .map(ResourceKey::location)
                .map(this::flattenPerksByLevel)
                .orElseGet(List::of);
    }

    public Collection<Perk> getPerksByType(PerkType perkType) {
        return perksByType.getOrDefault(perkType, List.of());
    }

    public <T> Collection<T> getPerksByType(Class<T> clazz) {
        return perksById.values().stream()
                .filter(clazz::isInstance)
                .map(clazz::cast)
                .collect(Collectors.toList());
    }

    public List<Perk> getPerksByProfession(ResourceKey<Profession> profession) {
        return flattenPerksByLevel(profession.location());
    }

    public Set<Perk> getPerksByProfession(Holder<Profession> profession, int level) {
        return profession.unwrapKey()
                .map(key -> getPerksByProfession(key, level))
                .orElseGet(HashSet::new);
    }

    public Set<Perk> getPerksByProfession(ResourceKey<Profession> profession, int level) {
        NavigableMap<Integer, List<Perk>> perksByLevel = perksByProfessionAndLevel.get(profession.location());
        if (perksByLevel == null) {
            return new HashSet<>();
        }
        return perksByLevel.headMap(level, true).values().stream().flatMap(Collection::stream).collect(Collectors.toSet());
    }

    private List<Perk> flattenPerksByLevel(ResourceLocation professionKey) {
        NavigableMap<Integer, List<Perk>> perksByLevel = perksByProfessionAndLevel.get(professionKey);
        if (perksByLevel == null) {
            return List.of();
        }
        return perksByLevel.values().stream()
                .flatMap(Collection::stream)
                .toList();
    }

    private static void addToNavigableMap(Map<ResourceLocation, NavigableMap<Integer, List<Perk>>> index, Perk perk) {
        perk.getProfession().unwrapKey()
                .map(ResourceKey::location)
                .ifPresent(key -> index
                        .computeIfAbsent(key, ignored -> new TreeMap<>())
                        .computeIfAbsent(perk.getLevelRequirement(), ignored -> new ArrayList<>())
                        .add(perk));
    }

    private static void removeOverriddenValueFromNavigableMap(Map<ResourceLocation, NavigableMap<Integer, List<Perk>>> index, Perk perk) {
        perk.getProfession().unwrapKey()
                .map(ResourceKey::location)
                .ifPresent(key -> {
                    NavigableMap<Integer, List<Perk>> byLevel = index.get(key);
                    if (byLevel == null) {
                        return;
                    }

                    List<Perk> perks = byLevel.get(perk.getLevelRequirement());
                    if (perks == null) {
                        return;
                    }

                    perks.remove(perk);
                    if (perks.isEmpty()) {
                        byLevel.remove(perk.getLevelRequirement());
                    }
                    if (byLevel.isEmpty()) {
                        index.remove(key);
                    }
                });
    }

    private static void addToTypeMap(Map<PerkType, List<Perk>> index, Perk perk) {
        index.computeIfAbsent(perk.getType(), ignored -> new ArrayList<>()).add(perk);
    }

    private static void removeFromPerkTypeMap(Map<PerkType, List<Perk>> index, Perk perk) {
        List<Perk> perks = index.get(perk.getType());
        if (perks == null) {
            return;
        }
        perks.remove(perk);
        if (perks.isEmpty()) {
            index.remove(perk.getType());
        }
    }

    public void setUnclaimedPerks(Occupation occupation, int level, IProfessionalPlayer player) {
        Set<Perk> perksForLevel = this.getPerksByProfession(occupation.getProfession(), level);
        perksForLevel.removeIf(perk -> occupation.hasClaimedPerk(perk.getId()));

        // todo; add an event here before we map it. then we map it and it gets set to the player
        Set<ResourceLocation> unclaimedPerksIds = perksForLevel.stream().map(Perk::getId).collect(Collectors.toSet());
        occupation.setUnclaimedPerks(unclaimedPerksIds);
        player.markDirty(true);
    }

    public void playerJoined(IProfessionalPlayer player, ServerPlayer serverPlayer) {



        Map<String, EnumMap<Perk.ModificationStage, Double>> groupedStageValues = new HashMap<>();
        Map<String, EnumMap<Perk.ModificationStage, Perk.Applicator>> applicators = new HashMap<>();


        for (Occupation activeOccupation : player.getActiveOccupations()) {

            List<ResourceLocation> disabledPerks = new ArrayList<>();

            for (ResourceLocation claimedPerk : activeOccupation.getClaimedPerks()) {
                Perk perk = this.getPerk(claimedPerk);

                // 1. Check if the perk is still valid, maybe it was removed after the player claimed it
                // 2. Maybe perk isn't null, but it possibly changed, so let's recalculate that it's still valid. if it's not, remove it.
                if (perk == null || Perk.PerkStatus.INVALID == perk.onRecalculate(activeOccupation, player, serverPlayer)) {
                    disabledPerks.add(claimedPerk);
                }
            }

            activeOccupation.removeAllClaimedPerks(disabledPerks);
            // 3. The player logs in, new perks have been added, we need to add these to the unclaimed ones.
            this.setUnclaimedPerks(activeOccupation, activeOccupation.getLevel(), player);

        }

        for (IStartupPerk startupPerk : this.getPerksByType(IStartupPerk.class)) {
            ResourceLocation id = startupPerk.getId();
            Perk.ModificationStage stage = startupPerk.getModificationStage();
            String perkGroup = getLogicalGroupId(startupPerk);
            ProfessionsCommon.LOG.debug("Perk We're Checking: {}, {}", id, perkGroup);

            if (player.hasClaimedPerk(id)) {
                ProfessionsCommon.LOG.debug("Player Claimed!");
                groupedStageValues.computeIfAbsent(perkGroup, s -> new EnumMap<>(Perk.ModificationStage.class))
                        .merge(stage, startupPerk.getValue(), Double::sum);
                Perk.Applicator put = applicators.computeIfAbsent(perkGroup, s -> new EnumMap<>(Perk.ModificationStage.class))
                        .put(stage, startupPerk.applicator(serverPlayer));
                ProfessionsCommon.LOG.debug("Overrode Previous Applicator: {}", put != null);
            }
        }

        for (Map.Entry<String, EnumMap<Perk.ModificationStage, Double>> entry : groupedStageValues.entrySet()) {
            ProfessionsCommon.LOG.debug("We're Modifying: {}", entry.getKey());
            String groupingId = entry.getKey();
            EnumMap<Perk.ModificationStage, Double> stageValues = entry.getValue();
            EnumMap<Perk.ModificationStage, Perk.Applicator> stageApplicators = applicators.get(groupingId);

            if (stageApplicators == null) {
                continue;
            }

            double startingValue = 0.0d;
            for (Perk.ModificationStage stage :  Perk.ModificationStage.values()) {
                Double stageValue = stageValues.get(stage);
                if (stageValue == null) {
                    continue;
                }

                Perk.Applicator applicator = stageApplicators.get(stage);
                if (applicator == null) {
                    continue;
                }

                ProfessionsCommon.LOG.debug("Stage: {}, Value: {}, Stage Value: {}", stage, startingValue, stageValue);
                startingValue += applicator.apply(stage, startingValue, stageValue);
                ProfessionsCommon.LOG.debug("After: {}", startingValue);
            }
        }

    }

    private static String getLogicalGroupId(IStartupPerk startupPerk) {
        ResourceLocation groupId = startupPerk.getGroupId();
        String stageSuffix = "_" + startupPerk.getModificationStage().getSerializedName();
        String path = groupId.getPath();
        if (path.endsWith(stageSuffix)) {
            path = path.substring(0, path.length() - stageSuffix.length());
        }
        return ResourceLocation.fromNamespaceAndPath(groupId.getNamespace(), path).toString();
    }
}

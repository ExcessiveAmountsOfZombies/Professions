package com.epherical.professions;

import com.epherical.professions.api.IProfessionalPlayer;
import com.epherical.professions.core.ProfessionCategory;
import com.epherical.professions.core.Profession;
import com.epherical.professions.api.actions.Gate;
import com.epherical.professions.model.gating.GateType;
import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public class GateManager {

    private static final Logger LOGGER = LogManager.getLogger();
    private static final String FEATURE_GATES_ENABLED = "gatesEnabled";

    private final Multimap<GateType, Gate<?>> gateMap = MultimapBuilder.hashKeys().hashSetValues().build();
    private final Multimap<Holder<Profession>, Gate<?>> professionToGatesMap = MultimapBuilder.hashKeys().hashSetValues().build();
    private final Multimap<Holder<?>, Gate<?>> valueToGatesMap = MultimapBuilder.hashKeys().hashSetValues().build();
    private final Multimap<Gate<?>, Holder<?>> gateToValueMap = MultimapBuilder.hashKeys().linkedHashSetValues().build();

    private boolean finished = false;
    private HolderLookup.Provider registryLookup;

    public GateManager(HolderLookup.Provider registryLookup) {
        this.registryLookup = registryLookup;
    }

    public void setRegistryLookup(HolderLookup.Provider registryLookup) {
        this.registryLookup = registryLookup;
    }

    public synchronized void reloadGates(List<Gate<?>> gates) {
        gateMap.clear();
        professionToGatesMap.clear();
        valueToGatesMap.clear();
        gateToValueMap.clear();
        finished = false;

        for (Gate<?> gate : gates) {
            gateMap.put(gate.getGateType(), gate);
            professionToGatesMap.put(gate.getProfession(), gate);
        }

        LOGGER.info("Reloaded {} gates", gates.size());
    }

    public Collection<Gate<?>> getGatesByType(GateType gateType) {
        if (!finished) {
            finish();
        }
        return gateMap.get(gateType);
    }

    public Collection<Gate<?>> getGatesByProfession(Holder<Profession> profession) {
        if (!finished) {
            finish();
        }
        return professionToGatesMap.get(profession);
    }

    public Collection<Gate<?>> getGatesByValue(Holder<?> value) {
        if (!finished) {
            finish();
        }
        return valueToGatesMap.get(value);
    }

    public Collection<Holder<?>> getValuesForGate(Gate<?> gate) {
        if (!finished) {
            finish();
        }
        return gateToValueMap.get(gate);
    }

    public boolean areGatesEnabled(@NotNull IProfessionalPlayer player) {
        ProfessionCategory category = player.getCategory();
        if (category == null) {
            return false;
        }

        ProfessionCategoryManager categoryManager = ProfessionsCommon.INSTANCE.getCategoryManager();
        Identifier categoryId = categoryManager.getCategoryId(category);
        if (categoryId == null) {
            return false;
        }

        return category.getFeatureValueOrDefault(Codec.BOOL, FEATURE_GATES_ENABLED, false)
                .result().orElse(false);
    }

    @SuppressWarnings("unchecked")
    private synchronized <T> void finish() {
        if (finished) {
            return;
        }

        if (registryLookup == null) {
            throw new IllegalStateException("Could not build gate value maps. Registry lookup is unavailable.");
        }

        for (Map.Entry<GateType, Gate<?>> entry : gateMap.entries()) {
            Gate<T> gate = (Gate<T>) entry.getValue();
            ResourceKey<? extends Registry<T>> registryKey = gate.getRegistryKey();
            HolderLookup.RegistryLookup<T> lookup = registryLookup.lookup(registryKey).orElse(null);
            if (lookup == null) {
                LOGGER.warn("Could not find registry lookup for {} while finishing gates. Gate {} will not be value-indexed.",
                        registryKey.identifier(), gate.getId());
                continue;
            }

            for (Either<TagKey<T>, ResourceKey<T>> value : gate.getValues()) {
                if (value.left().isPresent()) {
                    TagKey<T> tagKey = value.left().get();
                    for (Holder<T> holder : lookup.getOrThrow(tagKey)) {
                        valueToGatesMap.put(holder, gate);
                        gateToValueMap.put(gate, holder);
                    }
                    continue;
                }

                ResourceKey<T> resourceKey = value.right().orElseThrow();
                Holder.Reference<T> holder = lookup.getOrThrow(resourceKey);
                valueToGatesMap.put(holder, gate);
                gateToValueMap.put(gate, holder);
            }

            gate.getExtraValues(valueToGatesMap, gateToValueMap, registryLookup);
        }

        finished = true;
    }
}

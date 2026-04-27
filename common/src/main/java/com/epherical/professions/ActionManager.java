package com.epherical.professions;

import com.epherical.professions.core.Profession;
import com.epherical.professions.model.actions.Action;
import com.epherical.professions.model.actions.ActionType;
import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import com.mojang.datafixers.util.Either;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public class ActionManager {

    private static final Logger LOGGER = LogManager.getLogger();


    private Multimap<ActionType, Action<?>> actionMap = MultimapBuilder.hashKeys().hashSetValues().build();
    private Multimap<Holder<Profession>, Action<?>> professionToActionsMap = MultimapBuilder.hashKeys().hashSetValues().build();
    private Multimap<Holder<?>, Action<?>> valueToActionsMap = MultimapBuilder.hashKeys().hashSetValues().build();
    private Multimap<Action<?>, Holder<?>> actionToValueMap = MultimapBuilder.hashKeys().linkedHashSetValues().build();

    private boolean finished = false;

    private HolderLookup.Provider registryLookup;

    public ActionManager(HolderLookup.Provider registryLookup) {
        this.registryLookup = registryLookup;
    }

    public void setRegistryLookup(HolderLookup.Provider registryLookup) {
        this.registryLookup = registryLookup;
    }

    public void reloadActions(List<Action<?>> actions) {
        actionMap.clear();
        professionToActionsMap.clear();
        valueToActionsMap.clear();
        actionToValueMap.clear();
        finished = false;

        synchronized (this) {
            for (Action<?> action : actions) {
                actionMap.put(action.getType(), action);
                professionToActionsMap.put(action.getProfession(), action);
            }
            LOGGER.info("Reloaded {} actions", actions.size());
        }
    }

    public Collection<Action<?>> getActionsByType(ActionType actionType) {
        if (!finished) {
            finish();
        }
        return actionMap.get(actionType);
    }

    public Collection<Action<?>> getActionsByProfession(Holder<Profession> profession) {
        if (!finished) {
            finish();
        }

        return professionToActionsMap.get(profession);
    }

    public Collection<Action<?>> getActionsByValue(Holder<?> value) {
        if (!finished) {
            finish();
        }


        return valueToActionsMap.get(value);
    }

    public Collection<Holder<?>> getValuesForAction(Action<?> action) {
        if (!finished) {
            finish();
        }

        return actionToValueMap.get(action);
    }

    @SuppressWarnings("unchecked")
    private <T> void finish() {
        for (Map.Entry<ActionType, Action<?>> entry : actionMap.entries()) {
            Action<T> action = (Action<T>) entry.getValue();
            ResourceKey<? extends Registry<T>> registryKey = action.getRegistryKey();
            HolderLookup.RegistryLookup<T> lookup = registryLookup.lookup(registryKey)
                    .orElseThrow(() -> new IllegalStateException("Could not find registry lookup for " + registryKey.location()));

            for (Either<TagKey<T>, ResourceKey<T>> value : action.getValues()) {
                if (value.left().isPresent()) {
                    TagKey<T> tagKey = value.left().get();
                    for (Holder<T> holder : lookup.getOrThrow(tagKey)) {
                        valueToActionsMap.put(holder, action);
                        actionToValueMap.put(action, holder);
                    }
                } else {
                    ResourceKey<T> resourceKey = value.right().orElseThrow();
                    Holder.Reference<T> holder = lookup.getOrThrow(resourceKey);
                    valueToActionsMap.put(holder, action);
                    actionToValueMap.put(action, holder);
                }
            }
        }


        finished = true;
    }



}



package com.epherical.professions.datapack;

import com.epherical.professions.PlayerManager;
import com.epherical.professions.ProfessionsForge;
import com.epherical.professions.RegistryConstants;
import com.epherical.professions.profession.Profession;
import com.epherical.professions.profession.ProfessionBuilder;
import com.epherical.professions.profession.ProfessionEditorSerializer;
import com.epherical.professions.profession.ProfessionSerializer;
import com.epherical.professions.profession.action.Action;
import com.epherical.professions.profession.action.Actions;
import com.epherical.professions.profession.conditions.ActionCondition;
import com.epherical.professions.profession.conditions.ActionConditions;
import com.epherical.professions.profession.editor.Append;
import com.epherical.professions.profession.editor.Editor;
import com.epherical.professions.profession.rewards.Reward;
import com.epherical.professions.profession.rewards.Rewards;
import com.epherical.professions.profession.unlock.Unlock;
import com.epherical.professions.profession.unlock.Unlocks;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.mojang.logging.LogUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class ForgeProfLoader extends AbstractProfessionLoader implements CommonProfessionLoader {

    public ForgeProfLoader() {
        super("professions/occupations");
    }

    @Override
    public void apply(Map<ResourceLocation, JsonElement> object, ResourceManager resourceManager, ProfilerFiller profiler) {
        super.apply(object, resourceManager, profiler);
        PlayerManager manager = ProfessionsForge.getInstance().getPlayerManager();
        // this will be null when it first loads.
        if (manager != null) {
            ProfessionsForge.getInstance().getPlayerManager().reload();
        }
    }

    @SubscribeEvent
    public void onDataReload(AddReloadListenerEvent event) {
        event.addListener(this);
    }

    @Nullable
    public Profession getProfession(ResourceLocation location) {
        return professionMap.get(location);
    }

    public Collection<Profession> getProfessions() {
        return professionMap.values();
    }

    public Set<ResourceLocation> getProfessionKeys() {
        return professionMap.keySet();
    }


    @Nullable
    public ResourceLocation getIDFromProfession(Profession profession) {
        for (Map.Entry<ResourceLocation, Profession> entry : professionMap.entrySet()) {
            if (entry.getValue().getKey().equals(profession.getKey())) {
                return entry.getKey();
            }
        }
        return null;
    }

    public void clearProfessions() {
        professionMap = ImmutableMap.of();
    }

    public static JsonElement serialize(Profession profession) {
        return GSON.toJsonTree(profession);
    }

    public static JsonElement serialize(Editor unlock) {
        return GSON.toJsonTree(unlock);
    }
}

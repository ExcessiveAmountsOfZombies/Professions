package com.epherical.professions.datapack;

import com.epherical.professions.PlayerManager;
import com.epherical.professions.ProfessionConstants;
import com.epherical.professions.ProfessionsMod;
import com.epherical.professions.events.ProfessionUtilityEvents;
import com.epherical.professions.profession.Profession;
import com.epherical.professions.profession.ProfessionSerializer;
import com.epherical.professions.profession.action.Action;
import com.epherical.professions.profession.action.Actions;
import com.epherical.professions.profession.conditions.ActionCondition;
import com.epherical.professions.profession.conditions.ActionConditions;
import com.epherical.professions.profession.rewards.Reward;
import com.epherical.professions.profession.rewards.Rewards;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.logging.LogUtils;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

public class ProfessionLoader extends SimpleJsonResourceReloadListener implements IdentifiableResourceReloadListener {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Gson GSON = createProfessionSerializer()
            .setPrettyPrinting()
            .create();
    private Map<ResourceLocation, Profession> professionMap = ImmutableMap.of();


    public ProfessionLoader() {
        super(GSON, "professions/occupations");
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> object, ResourceManager resourceManager, ProfilerFiller profiler) {
        professionMap = null;

        ImmutableMap.Builder<ResourceLocation, Profession> builder = ImmutableMap.builder();

        object.forEach((location, jsonElement) -> {
            JsonObject jsonObject = GsonHelper.convertToJsonObject(jsonElement, "profession");
            if (jsonObject.has("type")) {
                ResourceLocation type = new ResourceLocation(GsonHelper.getAsString(jsonObject, "type"));
                ProfessionSerializer<? extends Profession> nullable = ProfessionConstants.PROFESSION_SERIALIZER.get(type);
                nullable = nullable != null ? nullable : ProfessionSerializer.DEFAULT_PROFESSION;
                Profession profession = GSON.fromJson(jsonElement, nullable.getType());
                profession.setKey(location);
                builder.put(location, profession);
            }
        });
        this.professionMap = builder.build();

        PlayerManager manager = ProfessionsMod.getInstance().getPlayerManager();
        // this will be null when it first loads.
        if (manager != null) {
            ProfessionsMod.getInstance().getPlayerManager().reload();
        }
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

    @Override
    public ResourceLocation getFabricId() {
        return new ResourceLocation("professions", "professions/occupations");
    }

    private static GsonBuilder createProfessionSerializer() {
        GsonBuilder builder = new GsonBuilder()
                .registerTypeHierarchyAdapter(Reward.class, Rewards.createGsonAdapter())
                .registerTypeHierarchyAdapter(ActionCondition.class, ActionConditions.createGsonAdapter())
                .registerTypeHierarchyAdapter(Action.class, Actions.createGsonAdapter())
                .registerTypeAdapter(Profession.class, new Profession.Serializer());
        ProfessionUtilityEvents.SERIALIZER_CALLBACK.invoker().addProfessionSerializer(builder);
        return builder;
    }

    public static JsonElement serialize(Profession profession) {
        return GSON.toJsonTree(profession);
    }

    public void loadProfessionsFromServer(Iterable<Profession> professions) {
        ImmutableMap.Builder<ResourceLocation, Profession> builder = ImmutableMap.builder();
        for (Profession profession : professions) {
            builder.put(profession.getKey(), profession);
        }
        this.professionMap = builder.build();
    }
}

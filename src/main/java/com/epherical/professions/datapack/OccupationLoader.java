package com.epherical.professions.datapack;

import com.epherical.professions.profession.Profession;
import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.mojang.logging.LogUtils;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import org.slf4j.Logger;

import java.util.Map;

public class OccupationLoader extends SimpleJsonResourceReloadListener implements IdentifiableResourceReloadListener {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping()
            .registerTypeAdapter(ResourceLocation.class, new ResourceLocation.Serializer())
            .create();
    private Map<ResourceLocation, Profession> professionMap = ImmutableMap.of();


    public OccupationLoader() {
        super(GSON, "professions/occupations");
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> object, ResourceManager resourceManager, ProfilerFiller profiler) {
        ImmutableMap.Builder<ResourceLocation, Profession> builder = ImmutableMap.builder();
    }

    @Override
    public ResourceLocation getFabricId() {
        return new ResourceLocation("professions", "professions/occupations");
    }
}

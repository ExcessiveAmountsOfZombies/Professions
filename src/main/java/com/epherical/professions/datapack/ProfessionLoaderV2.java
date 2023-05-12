package com.epherical.professions.datapack;

import com.epherical.professions.RegistryConstants;
import com.epherical.professions.profession.Profession;
import com.epherical.professions.profession.ProfessionBuilder;
import com.epherical.professions.profession.ProfessionSerializer;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.logging.LogUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;
import org.slf4j.Logger;

import java.util.Map;
import java.util.stream.Collectors;

import static com.epherical.professions.datapack.ProfessionLoader.GSON;

public class ProfessionLoaderV2 extends SimpleJsonResourceReloadListener {
    protected static final Logger LOGGER = LogUtils.getLogger();

    protected Map<ResourceLocation, Profession> professionMap = ImmutableMap.of();
    protected Map<ResourceLocation, ProfessionBuilder> builderMap = ImmutableMap.of();

    public ProfessionLoaderV2(String data) {
        super(GSON, data);
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> object, ResourceManager resourceManager, ProfilerFiller profilerFiller) {
        professionMap = null;

        Map<ResourceLocation, ProfessionBuilder> temp = Maps.newHashMap();

        object.forEach((location, jsonElement) -> {
            try {
                JsonObject jsonObject = GsonHelper.convertToJsonObject(jsonElement, "profession");
                if (jsonObject.has("type")) {
                    ResourceLocation type = new ResourceLocation(GsonHelper.getAsString(jsonObject, "type"));
                    ProfessionSerializer<? extends Profession, ? extends ProfessionBuilder> nullable = RegistryConstants.PROFESSION_SERIALIZER.get(type);
                    nullable = nullable != null ? nullable : ProfessionSerializer.DEFAULT_PROFESSION_V2;
                    ProfessionBuilder profession = GSON.fromJson(jsonElement, nullable.getBuilderType());
                    profession.setKey(location);
                    temp.put(location, profession);
                }
            } catch (Exception e) {
                LOGGER.warn("Could not finish deserializing profession {}, reason(s) for failing: {}", location.toString(), e.getMessage());
                throw e;
            }
        });
        builderMap = ImmutableMap.copyOf(temp);
    }


    public void finishLoading() {
        Map<ResourceLocation, Profession> result = builderMap.entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().build()));
        this.professionMap = ImmutableMap.copyOf(result);
    }


    public Map<ResourceLocation, ProfessionBuilder> getBuilderMap() {
        return builderMap;
    }

    public Map<ResourceLocation, Profession> getProfessionMap() {
        return professionMap;
    }
}

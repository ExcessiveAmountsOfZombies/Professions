package com.epherical.professions.datapack;

import com.epherical.professions.profession.ProfessionBuilder;
import com.epherical.professions.profession.operation.ObjectOperation;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;

import java.util.HashMap;
import java.util.Map;

import static com.epherical.professions.datapack.ProfessionLoader.GSON;

public class OperationLoader<OP extends ObjectOperation<?>, T> extends SimpleJsonResourceReloadListener {

    private final Class<? extends OP> clazz;

    private final String directory;

    protected Map<ResourceLocation, OP> operation = new HashMap<>();

    private final ResourceKey<? extends Registry<T>> resourceKey;

    public OperationLoader(String dataLocation, Class<? extends OP> clazz, ResourceKey<Registry<T>> resourceKey) {
        super(GSON, dataLocation);
        this.directory = dataLocation;
        this.clazz = clazz;
        // todo; maybe enforce key type.
        this.resourceKey = resourceKey;
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> object, ResourceManager resourceManager, ProfilerFiller profilerFiller) {
        object.forEach((resourceLocation, jsonElement) -> {
            JsonObject jsonObject = GsonHelper.convertToJsonObject(jsonElement, "dataLocation");
            try {
                OP type = GSON.fromJson(jsonObject, clazz);
                type.setKey((ResourceKey<? extends Registry<?>>) ResourceKey.create(resourceKey, resourceLocation));
                // TODO; Datapack reloading fails this could cause an issue. we could end up with inconsistent data.
                operation.put(resourceLocation, type);
            } catch (JsonSyntaxException e) {
                String fileLocation = resourceLocation.getNamespace() + "/" + directory + "/" + resourceLocation.getPath() + ".json";
                ProfessionLoader.LOGGER.warn("Operation could not be loaded, likely file is {}. ERROR: {}", fileLocation, e.getMessage());
                throw e;
            }
        });
    }

    public void finishLoading(MinecraftServer server, Map<ResourceLocation, ProfessionBuilder> builderMap) {
        for (OP value : operation.values()) {
            value.applyData(server, builderMap);
        }
    }

    public String getDirectory() {
        return directory;
    }
}

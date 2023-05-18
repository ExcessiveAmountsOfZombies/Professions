package com.epherical.professions.datapack;

import com.epherical.professions.profession.ProfessionBuilder;
import com.epherical.professions.profession.operation.AbstractOperation;
import com.epherical.professions.profession.operation.ObjectOperation;
import com.epherical.professions.profession.operation.TagOperation;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
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

public class OperationLoader<OP extends AbstractOperation<T>, T> extends SimpleJsonResourceReloadListener {

    private static Map<ResourceLocation, Class<?>> operationTypes = new HashMap<>();

    private final String directory;

    protected Map<ResourceLocation, OP> operation = new HashMap<>();

    private final ResourceKey<Registry<T>> resourceKey;

    public OperationLoader(String dataLocation, ResourceKey<Registry<T>> resourceKey) {
        super(GSON, dataLocation);
        this.directory = dataLocation;
        this.resourceKey = resourceKey;
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> object, ResourceManager resourceManager, ProfilerFiller profilerFiller) {
        object.forEach((resourceLocation, jsonElement) -> {
            JsonObject jsonObject = GsonHelper.convertToJsonObject(jsonElement, "dataLocation");
            try {
                // TODO; maybe we make our own extension of GsonHelper to give better errors?
                ResourceLocation operationType = new ResourceLocation(GsonHelper.getAsString(jsonObject, "type"));
                Class<OP> aClass = (Class<OP>) operationTypes.get(operationType);
                if (aClass != null) {
                    OP op = GSON.fromJson(jsonObject, aClass);
                    // TODO; Datapack reloading fails this could cause an issue. we could end up with inconsistent data.
                    op.setKey( resourceKey, resourceLocation);
                    operation.put(resourceLocation, op);
                }
            } catch (Exception e) {
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

    static {
        operationTypes.put(new ResourceLocation("professions:item"), ObjectOperation.class);
        operationTypes.put(new ResourceLocation("professions:tag"), TagOperation.class);
    }
}

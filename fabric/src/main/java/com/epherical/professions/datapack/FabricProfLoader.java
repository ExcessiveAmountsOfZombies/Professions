package com.epherical.professions.datapack;

import com.epherical.professions.PlayerManager;
import com.epherical.professions.ProfessionsFabric;
import com.google.gson.JsonElement;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;

import java.util.Map;

public class FabricProfLoader extends ProfessionLoader implements IdentifiableResourceReloadListener {

    public FabricProfLoader() {
        super();
    }

    @Override
    public void apply(Map<ResourceLocation, JsonElement> object, ResourceManager resourceManager, ProfilerFiller profiler) {
        super.apply(object, resourceManager, profiler);
        PlayerManager manager = ProfessionsFabric.getInstance().getPlayerManager();
        // this will be null when it first loads.
        if (manager != null) {
            ProfessionsFabric.getInstance().getPlayerManager().reload();
        }
    }

    @Override
    public ResourceLocation getFabricId() {
        return new ResourceLocation("professions", "professions/occupations");
    }
}

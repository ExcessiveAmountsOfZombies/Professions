package com.epherical.professions.datapack;

import com.epherical.professions.PlayerManager;
import com.epherical.professions.ProfessionsForge;
import com.google.gson.JsonElement;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.Map;

public class ForgeProfLoader extends AbstractProfessionLoader implements CommonProfessionLoader {

    public ForgeProfLoader() {
        super();
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

}

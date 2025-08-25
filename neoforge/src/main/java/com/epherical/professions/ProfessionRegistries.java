package com.epherical.professions;

import com.epherical.professions.core.Profession;
import com.epherical.professions.registries.ActionLoader;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.neoforged.neoforge.registries.DataPackRegistryEvent;

import static com.epherical.professions.ProfessionsMod.PROFESSION_REGISTRY_KEY;

@EventBusSubscriber(modid = Constants.MOD_ID)
public final class ProfessionRegistries {






    /*@SubscribeEvent
    public static void registerDatapackRegistry(DataPackRegistryEvent.NewRegistry event) {
        event.dataPackRegistry(
                PROFESSION_REGISTRY_KEY,
                Profession.CODEC,
                Profession.CODEC // todo; we may network serialize less data
        );
    }*/

    @SubscribeEvent
    public static void registerDataReloader(AddReloadListenerEvent event) {
        event.addListener(new ActionLoader(event.getRegistryAccess()));
    }
}

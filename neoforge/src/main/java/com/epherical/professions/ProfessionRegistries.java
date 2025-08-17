package com.epherical.professions;

import com.epherical.professions.core.Profession;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.DataPackRegistryEvent;
import net.neoforged.neoforge.registries.DeferredRegister;

@EventBusSubscriber(modid = Constants.MOD_ID)
public final class ProfessionRegistries {

    public static final ResourceKey<Registry<Profession>> PROFESSION_REGISTRY_KEY =
            ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "professions/occupations"));


    public static final DeferredRegister<Profession> PROFESSION_REGISTER = DeferredRegister.create(PROFESSION_REGISTRY_KEY, Constants.MOD_ID);


    @SubscribeEvent
    public static void registerDatapackRegistry(DataPackRegistryEvent.NewRegistry event) {
        event.dataPackRegistry(
                PROFESSION_REGISTRY_KEY,
                Profession.CODEC,
                Profession.CODEC // todo; we may network serialize less data
        );
    }
}

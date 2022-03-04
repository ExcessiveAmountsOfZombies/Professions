package com.epherical.professions;

import com.epherical.octoecon.api.Economy;
import com.epherical.octoecon.api.event.EconomyEvents;
import com.epherical.professions.profession.ProfessionSerializer;
import com.epherical.professions.profession.action.ActionType;
import com.mojang.serialization.Lifecycle;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

public class ProfessionsMod implements ModInitializer {


    private @Nullable Economy economy;

    public static final ResourceKey<Registry<Object>> PROFESSION_KEY = ResourceKey.createRegistryKey(new ResourceLocation("professions", "professions/occupations"));
    public static final ResourceKey<Registry<ProfessionSerializer<?>>> PROFESSION_TYPE_KEY = ResourceKey.createRegistryKey(new ResourceLocation("professions", "professions/occupation_type"));
    public static final ResourceKey<Registry<ActionType>> ACTIONS_KEY = ResourceKey.createRegistryKey(new ResourceLocation("professions", "professions/actions"));
    public static final ResourceKey<Registry<Object>> CONDITIONS_KEY = ResourceKey.createRegistryKey(new ResourceLocation("professions", "professions/conditions"));
    // this might not be data driven? This just defines what will be executed on a successful 'action'
    public static final ResourceKey<Registry<Object>> RESULTS_KEY = ResourceKey.createRegistryKey(new ResourceLocation("professions", "professions/results"));

    public static final Registry<ProfessionSerializer<?>> PROFESSION_SERIALIZER = FabricRegistryBuilder.from(new MappedRegistry<>(PROFESSION_TYPE_KEY,
            Lifecycle.experimental(), null)).buildAndRegister();
    public static final Registry<ActionType> PROFESSION_ACTIONS = FabricRegistryBuilder.from(new MappedRegistry<>(ACTIONS_KEY,
            Lifecycle.experimental(), null)).buildAndRegister();

    @Override
    public void onInitialize() {
        EconomyEvents.ECONOMY_CHANGE_EVENT.register(economy -> {
            this.economy = economy;
        });
    }
}

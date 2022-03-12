package com.epherical.professions;

import com.epherical.octoecon.api.Economy;
import com.epherical.octoecon.api.event.EconomyEvents;
import com.epherical.professions.api.ProfessionalPlayer;
import com.epherical.professions.data.FileStorage;
import com.epherical.professions.data.Storage;
import com.epherical.professions.datapack.ProfessionLoader;
import com.epherical.professions.profession.Profession;
import com.epherical.professions.profession.ProfessionSerializer;
import com.epherical.professions.profession.action.ActionType;
import com.epherical.professions.profession.conditions.ActionConditionType;
import com.epherical.professions.profession.rewards.RewardType;
import com.mojang.serialization.Lifecycle;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.world.level.storage.LevelResource;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class ProfessionsMod implements ModInitializer {
    public static final String MOD_ID = "professions";

    private static ProfessionsMod mod;

    private static @Nullable Economy economy;
    private Storage<ProfessionalPlayer, UUID> dataStorage;

    public static final ResourceKey<Registry<Object>> PROFESSION_KEY = ResourceKey.createRegistryKey(modID("professions/occupations"));
    public static final ResourceKey<Registry<ProfessionSerializer<?>>> PROFESSION_TYPE_KEY = ResourceKey.createRegistryKey(modID("professions/occupation_type"));
    public static final ResourceKey<Registry<ActionType>> ACTION_TYPE_KEY = ResourceKey.createRegistryKey(modID("professions/actions"));
    public static final ResourceKey<Registry<ActionConditionType>> ACTION_CONDITION_KEY = ResourceKey.createRegistryKey(modID("professions/conditions"));
    public static final ResourceKey<Registry<RewardType>> REWARD_KEY = ResourceKey.createRegistryKey(modID("professions/rewards"));

    public static final Registry<ProfessionSerializer<? extends Profession>> PROFESSION_SERIALIZER = FabricRegistryBuilder.from(new MappedRegistry<>(PROFESSION_TYPE_KEY,
            Lifecycle.experimental(), null)).buildAndRegister();
    public static final Registry<ActionType> ACTION_TYPE = FabricRegistryBuilder.from(new MappedRegistry<>(ACTION_TYPE_KEY,
            Lifecycle.experimental(), null)).buildAndRegister();
    public static final Registry<ActionConditionType> ACTION_CONDITION_TYPE = FabricRegistryBuilder.from(new MappedRegistry<>(ACTION_CONDITION_KEY,
            Lifecycle.experimental(), null)).buildAndRegister();
    public static final Registry<RewardType> REWARDS = FabricRegistryBuilder.from(new MappedRegistry<>(REWARD_KEY,
            Lifecycle.experimental(), null)).buildAndRegister();

    public static final ProfessionLoader professionLoader = new ProfessionLoader();

    @Override
    public void onInitialize() {
        mod = this;
        init();
        ResourceManagerHelper.get(PackType.SERVER_DATA).registerReloadListener(professionLoader);
        EconomyEvents.ECONOMY_CHANGE_EVENT.register(economy -> {
            this.economy = economy;
        });
        ServerLifecycleEvents.SERVER_STARTING.register(server -> {
            dataStorage = new FileStorage(server.getWorldPath(LevelResource.ROOT).resolve("/professions/playerdata"));
        });
    }

    public static ResourceLocation modID(String name) {
        return new ResourceLocation(MOD_ID, name);
    }

    private static ProfessionSerializer<?> init() {
        return ProfessionSerializer.DEFAULT_PROFESSION;
    }

    public static Economy getEconomy() {
        return economy;
    }

    public Storage<ProfessionalPlayer, UUID> getDataStorage() {
        return dataStorage;
    }

    public static ProfessionsMod getInstance() {
        return mod;
    }
}
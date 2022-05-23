package com.epherical.professions;

import com.epherical.professions.profession.Profession;
import com.epherical.professions.profession.ProfessionSerializer;
import com.epherical.professions.profession.action.ActionType;
import com.epherical.professions.profession.action.Actions;
import com.epherical.professions.profession.conditions.ActionConditionType;
import com.epherical.professions.profession.conditions.ActionConditions;
import com.epherical.professions.profession.rewards.RewardType;
import com.epherical.professions.profession.rewards.Rewards;
import com.mojang.serialization.Lifecycle;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;

import static com.epherical.professions.Constants.modID;

public class RegistryConstants {

    public static final ResourceKey<Registry<ProfessionSerializer<?>>> PROFESSION_TYPE_KEY = ResourceKey.createRegistryKey(modID("professions/occupation_type"));
    public static final ResourceKey<Registry<ActionType>> ACTION_TYPE_KEY = ResourceKey.createRegistryKey(modID("professions/actions"));
    public static final ResourceKey<Registry<ActionConditionType>> ACTION_CONDITION_KEY = ResourceKey.createRegistryKey(modID("professions/conditions"));
    public static final ResourceKey<Registry<RewardType>> REWARD_KEY = ResourceKey.createRegistryKey(modID("professions/rewards"));


    public static IForgeRegistry<ProfessionSerializer<? extends Profession>> PROFESSION_SERIALIZER;
    // TODO: these might cause problems
    public static IForgeRegistry<ActionType> ACTION_TYPE;
    public static IForgeRegistry<ActionConditionType> ACTION_CONDITION_TYPE;
    public static IForgeRegistry<RewardType> REWARDS;

    @SubscribeEvent
    public static void createRegistries(RegistryEvent.NewRegistry event) {
        PROFESSION_SERIALIZER = new RegistryBuilder<ProfessionSerializer<?>>()
                .setType(c(ProfessionSerializer.class))
                .onClear((owner, stage) -> {
                    owner.setSlaveMap(modID("occupation_type"), new MappedRegistry<>(PROFESSION_TYPE_KEY, Lifecycle.experimental(), null));
                })
                .onCreate((owner, stage) -> {
                    owner.setSlaveMap(modID("occupation_type"), new MappedRegistry<>(PROFESSION_TYPE_KEY, Lifecycle.experimental(), null));
                })
                .onAdd((owner, stage, id, obj, oldObj) -> {
                    MappedRegistry<ProfessionSerializer<?>> registry = owner.getSlaveMap(modID("occupation_type"), MappedRegistry.class);
                    Registry.register(registry, obj.getRegistryName(), obj);
                })
                .disableSync().allowModification().disableSaving().setName(modID("professions/occupation_type")).create();
        ACTION_TYPE = new RegistryBuilder<ActionType>()
                .setType(ActionType.class)
                .onClear((owner, stage) -> {
                    owner.setSlaveMap(modID("actions"), new MappedRegistry<>(ACTION_TYPE_KEY, Lifecycle.experimental(), null));
                })
                .onCreate((owner, stage) -> {
                    owner.setSlaveMap(modID("actions"), new MappedRegistry<>(ACTION_TYPE_KEY, Lifecycle.experimental(), null));
                })
                .onAdd((owner, stage, id, obj, oldObj) -> {
                    MappedRegistry<ActionType> registry = owner.getSlaveMap(modID("actions"), MappedRegistry.class);
                    Registry.register(registry, obj.getRegistryName(), obj);
                })
                .disableSync().allowModification().disableSaving().setName(modID("professions/actions")).create();
        ACTION_CONDITION_TYPE = new RegistryBuilder<ActionConditionType>()
                .setType(ActionConditionType.class)
                .onClear((owner, stage) -> {
                    owner.setSlaveMap(modID("conditions"), new MappedRegistry<>(ACTION_CONDITION_KEY, Lifecycle.experimental(), null));
                })
                .onCreate((owner, stage) -> {
                    owner.setSlaveMap(modID("conditions"), new MappedRegistry<>(ACTION_CONDITION_KEY, Lifecycle.experimental(), null));
                })
                .onAdd((owner, stage, id, obj, oldObj) -> {
                    MappedRegistry<ActionConditionType> registry = owner.getSlaveMap(modID("conditions"), MappedRegistry.class);
                    Registry.register(registry, obj.getRegistryName(), obj);
                })
                .disableSync().allowModification().disableSaving().setName(modID("professions/conditions"))
                .create();
        REWARDS = new RegistryBuilder<RewardType>()
                .setType(RewardType.class)
                .onClear((owner, stage) -> {
                    owner.setSlaveMap(modID("rewards"), new MappedRegistry<>(REWARD_KEY, Lifecycle.experimental(), null));
                })
                .onCreate((owner, stage) -> {
                    owner.setSlaveMap(modID("rewards"), new MappedRegistry<>(REWARD_KEY, Lifecycle.experimental(), null));
                })
                .onAdd((owner, stage, id, obj, oldObj) -> {
                    MappedRegistry<RewardType> registry = owner.getSlaveMap(modID("rewards"), MappedRegistry.class);
                    Registry.register(registry, obj.getRegistryName(), obj);
                })
                .disableSync().allowModification().disableSaving().setName(modID("professions/rewards")).create();
        ProfessionSerializer.SERIALIZERS.register(FMLJavaModLoadingContext.get().getModEventBus());
        ActionConditions.CONDITIONS.register(FMLJavaModLoadingContext.get().getModEventBus());
        Actions.ACTIONS.register(FMLJavaModLoadingContext.get().getModEventBus());
        Rewards.REWARDS.register(FMLJavaModLoadingContext.get().getModEventBus());
    }
    // lul we copying forge code
    private static <T> Class<T> c(Class<?> cls) {
        return (Class<T>)cls;
    }
}

package com.epherical.professions;

import com.mojang.serialization.Lifecycle;
import net.minecraft.core.MappedRegistry;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.NewRegistryEvent;

import static com.epherical.professions.RegistryConstants.*;

public class ForgeRegConstants {

    @SubscribeEvent
    public static void createRegistries(NewRegistryEvent event) {
        PROFESSION_SERIALIZER = new MappedRegistry<>(PROFESSION_TYPE_KEY, Lifecycle.experimental(), null);
        PROFESSION_EDITOR_SERIALIZER = new MappedRegistry<>(PROFESSION_EDITOR_TYPE_KEY, Lifecycle.experimental(), null);
        ACTION_TYPE = new MappedRegistry<>(ACTION_TYPE_KEY, Lifecycle.experimental(), null);
        ACTION_CONDITION_TYPE = new MappedRegistry<>(ACTION_CONDITION_KEY, Lifecycle.experimental(), null);
        REWARDS = new MappedRegistry<>(REWARD_KEY, Lifecycle.experimental(), null);
        UNLOCKS = new MappedRegistry<>(UNLOCK_KEY, Lifecycle.experimental(), null);
        UNLOCK_TYPE = new MappedRegistry<>(UNLOCK_TYPE_KEY, Lifecycle.experimental(), null);

        /*PROFESSION_SERIALIZER = event.create(new RegistryBuilder<ProfessionSerializer<?>>()
                .onClear((owner, stage) -> {
                    owner.setSlaveMap(PROFESSION_TYPE_KEY.location(), new MappedRegistry<>(PROFESSION_TYPE_KEY, Lifecycle.experimental(), null));
                })
                .onCreate((owner, stage) -> {
                    owner.setSlaveMap(PROFESSION_TYPE_KEY.location(), new MappedRegistry<>(PROFESSION_TYPE_KEY, Lifecycle.experimental(), null));
                })
                .onAdd((owner, stage, id, key, obj, oldObj) -> {
                    MappedRegistry<ProfessionSerializer<?>> registry = owner.getSlaveMap(PROFESSION_TYPE_KEY.location(), MappedRegistry.class);
                    Registry.register(registry, key, obj);
                })
                .disableSync().allowModification().disableSaving().setName(PROFESSION_TYPE_KEY.location())).get();

        ACTION_TYPE = event.create(new RegistryBuilder<ActionType>()
                .onClear((owner, stage) -> {
                    owner.setSlaveMap(ACTION_TYPE_KEY.location(), new MappedRegistry<>(ACTION_TYPE_KEY, Lifecycle.experimental(), null));
                })
                .onCreate((owner, stage) -> {
                    owner.setSlaveMap(ACTION_TYPE_KEY.location(), new MappedRegistry<>(ACTION_TYPE_KEY, Lifecycle.experimental(), null));
                })
                .onAdd((owner, stage, id, key, obj, oldObj) -> {
                    MappedRegistry<ActionType> registry = owner.getSlaveMap(ACTION_TYPE_KEY.location(), MappedRegistry.class);
                    Registry.register(registry, key, obj);
                })
                .disableSync().allowModification().disableSaving().setName(ACTION_TYPE_KEY.location())).get();

        ACTION_CONDITION_TYPE = event.create(new RegistryBuilder<ActionConditionType>()
                .onClear((owner, stage) -> {
                    owner.setSlaveMap(ACTION_CONDITION_KEY.location(), new MappedRegistry<>(ACTION_CONDITION_KEY, Lifecycle.experimental(), null));
                })
                .onCreate((owner, stage) -> {
                    owner.setSlaveMap(ACTION_CONDITION_KEY.location(), new MappedRegistry<>(ACTION_CONDITION_KEY, Lifecycle.experimental(), null));
                })
                .onAdd((owner, stage, id, key, obj, oldObj) -> {
                    MappedRegistry<ActionConditionType> registry = owner.getSlaveMap(ACTION_CONDITION_KEY.location(), MappedRegistry.class);
                    Registry.register(registry, key, obj);
                })
                .disableSync().allowModification().disableSaving().setName(ACTION_CONDITION_KEY.location())).get();

        REWARDS = event.create(new RegistryBuilder<RewardType>()
                .onClear((owner, stage) -> {
                    owner.setSlaveMap(REWARD_KEY.location(), new MappedRegistry<>(REWARD_KEY, Lifecycle.experimental(), null));
                })
                .onCreate((owner, stage) -> {
                    owner.setSlaveMap(REWARD_KEY.location(), new MappedRegistry<>(REWARD_KEY, Lifecycle.experimental(), null));
                })
                .onAdd((owner, stage, id, key, obj, oldObj) -> {
                    MappedRegistry<RewardType> registry = owner.getSlaveMap(REWARD_KEY.location(), MappedRegistry.class);
                    Registry.register(registry, key, obj);
                })
                .disableSync().allowModification().disableSaving().setName(REWARD_KEY.location())).get();*/




        /*ProfessionSerializer.SERIALIZERS.register(FMLJavaModLoadingContext.get().getModEventBus());
        ActionConditions.CONDITIONS.register(FMLJavaModLoadingContext.get().getModEventBus());
        Actions.ACTIONS.register(FMLJavaModLoadingContext.get().getModEventBus());
        Rewards.REWARDS.register(FMLJavaModLoadingContext.get().getModEventBus());*/
    }
}

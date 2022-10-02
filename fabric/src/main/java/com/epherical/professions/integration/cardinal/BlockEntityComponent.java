package com.epherical.professions.integration.cardinal;

import dev.onyxstudios.cca.api.v3.block.BlockComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.block.BlockComponentInitializer;
import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistryV3;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.level.block.entity.BrewingStandBlockEntity;

public class BlockEntityComponent implements BlockComponentInitializer {

    public static final ComponentKey<PlayerOwning> PLAYER_OWNABLE = ComponentRegistryV3.INSTANCE.getOrCreate(
            new ResourceLocation("professions:ownable_be"), PlayerOwning.class);

    @Override
    public void registerBlockComponentFactories(BlockComponentFactoryRegistry registry) {
        registry.registerFor(AbstractFurnaceBlockEntity.class, PLAYER_OWNABLE, PlayerOwning::new);
        registry.registerFor(BrewingStandBlockEntity.class, PLAYER_OWNABLE, PlayerOwning::new);
        REGISTER_OWNABLE_BE_EVENT.invoker().registerOwnable(registry, PLAYER_OWNABLE);
    }

    public static final Event<RegisterOwnableBE> REGISTER_OWNABLE_BE_EVENT = EventFactory.createArrayBacked(RegisterOwnableBE.class, calls -> (registry, key) -> {
        for (RegisterOwnableBE call : calls) {
            call.registerOwnable(registry, key);
        }
    });

    public interface RegisterOwnableBE {
        void registerOwnable(BlockComponentFactoryRegistry registry, ComponentKey<PlayerOwning> key);
    }
}

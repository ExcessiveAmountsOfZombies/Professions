package com.epherical.professions;


import com.epherical.professions.core.Profession;
import com.epherical.professions.core.actions.Action;
import com.epherical.professions.core.actions.ActionType;
import com.epherical.professions.core.progression.ProfessionalPlayer;
import com.epherical.professions.registries.ActionLoader;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import net.neoforged.neoforge.registries.NewRegistryEvent;
import net.neoforged.neoforge.registries.RegistryBuilder;

import java.util.ArrayList;
import java.util.function.Supplier;

@Mod(Constants.MOD_ID)
public class ProfessionsMod {

    public static final ResourceKey<Registry<Profession>> PROFESSION_REGISTRY_KEY =
            ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "professions/occupations"));


    public static final ResourceKey<Registry<ActionType>> ACTION_REGISTRY_KEY =
            ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "actions"));


    public static Registry<ActionType> ACTIONS = new RegistryBuilder<>(ACTION_REGISTRY_KEY)
            .sync(true).create();




    public static final DeferredRegister<Profession> PROFESSION_REGISTER = DeferredRegister.create(PROFESSION_REGISTRY_KEY, Constants.MOD_ID);
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENTS_REGISTER = DeferredRegister.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, Constants.MOD_ID);

    public static final Supplier<AttachmentType<ProfessionalPlayer>> PROFESSIONAL_PLAYER = ATTACHMENTS_REGISTER.register(
            "professional_player", () -> AttachmentType.builder(() -> new ProfessionalPlayer(new ArrayList<>()))
                    //.sync()
                    //.serialize()
                    //.copyOnDeath()
                    .build()
    );


    public ProfessionsMod(IEventBus eventBus) {

        PROFESSION_REGISTER.register(eventBus);
        ATTACHMENTS_REGISTER.register(eventBus);

        CommonClass.init();

    }



    @EventBusSubscriber
    public static class EventHandler {
        @SubscribeEvent
        public static void onRegistryCreate(NewRegistryEvent event) {
            event.register(ACTIONS);
            //ACTIONS = event.create(new RegistryBuilder<>( ACTION_REGISTRY_KEY).sync(true));
        }

        @SubscribeEvent
        public static void onDataReload(AddReloadListenerEvent event) {
            event.addListener(new ActionLoader(event.getRegistryAccess()));
        }
    }
}

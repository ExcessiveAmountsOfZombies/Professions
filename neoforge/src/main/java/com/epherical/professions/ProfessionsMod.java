package com.epherical.professions;


import com.epherical.professions.api.IProfessionalPlayer;
import com.epherical.professions.commands.ProfessionsStandardCommands;
import com.epherical.professions.core.Profession;
import com.epherical.professions.core.actions.ActionType;
import com.epherical.professions.core.conditions.ConditionType;
import com.epherical.professions.core.context.ProfessionContext;
import com.epherical.professions.core.context.ProfessionParameter;
import com.epherical.professions.core.progression.OccupationSlot;
import com.epherical.professions.core.progression.ProfessionalPlayer;
import com.epherical.professions.core.register.Actions;
import com.epherical.professions.core.register.PlatformBootstrap;
import com.epherical.professions.core.rewards.RewardType;
import com.epherical.professions.registries.ActionLoad2;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.registries.DataPackRegistryEvent;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import net.neoforged.neoforge.registries.NewRegistryEvent;
import net.neoforged.neoforge.registries.RegisterEvent;
import net.neoforged.neoforge.registries.RegistryBuilder;

import java.io.File;
import java.util.ArrayList;
import java.util.function.Supplier;


@Mod(Constants.MOD_ID)
public class ProfessionsMod extends CommonClass {

    private static final NeoForgeRegistrarBackend NEO_FORGE_REGISTRAR_BACKEND = new NeoForgeRegistrarBackend();

    public static Registry<ActionType> ACTIONS;
    public static Registry<ConditionType> CONDITIONS;
    public static Registry<RewardType> REWARDS;

    public static final DeferredRegister<Profession> PROFESSION_REGISTER = DeferredRegister.create(PROFESSION_REGISTRY_KEY, Constants.MOD_ID);
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENTS_REGISTER = DeferredRegister.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, Constants.MOD_ID);

    public static RegistryAccess REGISTRY_ACCESS = null;


    public static ProfessionsMod mod;

    public static final Supplier<AttachmentType<IProfessionalPlayer>> PROFESSIONAL_PLAYER = ATTACHMENTS_REGISTER.register(
            "professional_player", () -> AttachmentType.builder(() -> {
                        IProfessionalPlayer professionalPlayer = new ProfessionalPlayer(new ArrayList<>());
                        REGISTRY_ACCESS.registry(PROFESSION_REGISTRY_KEY).ifPresent(professions ->
                                professions.holders().forEach(profession ->
                                        professionalPlayer.joinOccupation(profession, OccupationSlot.ACTIVE)));
                        return professionalPlayer;
                    })
                    //.sync()
                    .serialize(ProfessionalPlayer.CODEC)
                    .copyOnDeath()
                    .build()
    );


    public ProfessionsMod(IEventBus eventBus) {
        this.init();
        this.buildConfig();

        mod = this;
        PlatformBootstrap.init(NEO_FORGE_REGISTRAR_BACKEND);
        PROFESSION_REGISTER.register(eventBus);
        ATTACHMENTS_REGISTER.register(eventBus);

    }

    /*@Override
    public PlayerManager getPlayerManager() {
        return null;
    }*/

    @Override
    public ActionLoad2 getActionLoader() {
        return ACTION_LOAD2;
    }

    @Override
    public File getModDir() {
        return FMLPaths.CONFIGDIR.get().toFile();
    }

    @Override
    public boolean isClientEnvironment() {
        return FMLEnvironment.dist.isClient();
    }


    @EventBusSubscriber(modid =  Constants.MOD_ID)
    public static class EventHandler {


        @SubscribeEvent
        public static void registerDatapackRegistry(DataPackRegistryEvent.NewRegistry event) {
            event.dataPackRegistry(
                    PROFESSION_REGISTRY_KEY,
                    Profession.CODEC,
                    Profession.CODEC
                    //professionRegistryBuilder -> professionRegistryBuilder.sync(true).create()
            );
        }


        @SubscribeEvent
        public static void onRegistryCreate(NewRegistryEvent event) {
            event.register(ACTIONS = new RegistryBuilder<>(ACTION_REGISTRY_KEY).sync(true).create());
            event.register(CONDITIONS = new RegistryBuilder<>(CONDITION_REGISTRY_KEY).sync(true).create());
            event.register(REWARDS = new RegistryBuilder<>(REWARD_REGISTRY_KEY).sync(true).create());
            CommonClass.register();
        }

        @SubscribeEvent
        public static void onRegisterEvent(RegisterEvent event) {
            ProfessionsMod.NEO_FORGE_REGISTRAR_BACKEND.onRegister(event);
        }

        @SubscribeEvent
        public static void onDataReload(AddReloadListenerEvent event) {
            //event.addListener(new ActionLoader(event.getRegistryAccess()));
            ActionLoad2 loader = new ActionLoad2(event.getRegistryAccess());
            event.addListener(loader);
            ACTION_LOAD2 = loader;
            REGISTRY_ACCESS = event.getRegistryAccess();
        }

        @SubscribeEvent
        public static void onCommandRegister(RegisterCommandsEvent event) {
            new ProfessionsStandardCommands(ProfessionsMod.mod, event.getDispatcher(), event.getBuildContext());
        }


        @SubscribeEvent
        public static void onBlockBreak(BlockEvent.BreakEvent event) {
            Holder<Block> blockHolder = event.getState().getBlockHolder();
            Player player = event.getPlayer();
            IProfessionalPlayer iProfessionalPlayer = player.getData(PROFESSIONAL_PLAYER);

            if (!player.isCreative()) {
                ProfessionContext context = new ProfessionContext.Builder(null)
                        .addParameter(ProfessionParameter.ACTION_TYPE, Actions.BLOCK_BREAK)
                        .addParameter(ProfessionParameter.THIS_PLAYER, iProfessionalPlayer)
                        .addParameter(ProfessionParameter.THIS_BLOCK, event.getState())
                        .addParameter(ProfessionParameter.BLOCKPOS, event.getPos())
                        .addParameter(ProfessionParameter.ITEM_INVOLVED, event.getPlayer().getWeaponItem())
                        .addParameter(ProfessionParameter.THIS_HOLDER, blockHolder)
                        .build();

                iProfessionalPlayer.handleAction(context, blockHolder);
            }
            player.setData(PROFESSIONAL_PLAYER, iProfessionalPlayer);

        }
    }
}

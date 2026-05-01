package com.epherical.professions.client;


import com.epherical.professions.ProfessionCategoryManager;
import com.epherical.professions.ProfessionsCommon;
import com.epherical.professions.model.Occupation;
import com.epherical.professions.networking.S2CExperienceGainPayload;
import com.epherical.professions.presentation.client.gui.screen.OccupationCategorySelectionScreen;
import com.epherical.professions.presentation.client.gui.screen.OccupationMenuScreen;
import com.epherical.professions.presentation.client.notification.ExperienceNotificationHandler;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import org.lwjgl.glfw.GLFW;

import java.util.List;
import java.util.Locale;

@EventBusSubscriber(value = Dist.CLIENT, modid = ProfessionsCommon.MOD_ID)
public class ClientInitializer {

    private static KeyMapping occupationMenu;



    @SubscribeEvent
    public static void registerKeys(RegisterKeyMappingsEvent event) {
        event.register(occupationMenu = new KeyMapping(
                "key.professions.open_occupation_menu",
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_R,
                "category.professions.occupation"));
    }


    @SubscribeEvent
    public static void handleInput(InputEvent.Key event) {
        if (occupationMenu.consumeClick()) {
            Minecraft mc = Minecraft.getInstance();
            if (ProfessionsCommon.INSTANCE.getPlayerManager().getPlayer(mc.getUser().getProfileId()).getCategory() != null) {
                List<Occupation> activeOccupations = ProfessionsCommon.INSTANCE.getPlayerManager().getPlayer(mc.getUser().getProfileId()).getActiveOccupations();
                mc.setScreen(new OccupationMenuScreen(activeOccupations));
            } else {
                ProfessionCategoryManager categoryManager = ProfessionsCommon.INSTANCE.getCategoryLoader().getCategoryManager();
                mc.setScreen(new OccupationCategorySelectionScreen(categoryManager.getCategories()));
            }
        }
    }

    private static final ResourceLocation PROFESSION_XP =
            ResourceLocation.fromNamespaceAndPath(ProfessionsCommon.MOD_ID, "profession_xp");

    @SubscribeEvent
    public static void registerLayer(RegisterGuiLayersEvent event) {
        event.registerAbove(VanillaGuiLayers.HOTBAR, PROFESSION_XP, ExperienceNotificationHandler::render);
    }



}

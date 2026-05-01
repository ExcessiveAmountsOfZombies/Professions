package com.epherical.professions.client;


import com.epherical.professions.ProfessionCategoryManager;
import com.epherical.professions.ProfessionsCommon;
import com.epherical.professions.model.Occupation;
import com.epherical.professions.presentation.client.gui.screen.OccupationCategorySelectionScreen;
import com.epherical.professions.presentation.client.gui.screen.OccupationMenuScreen;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import org.lwjgl.glfw.GLFW;

import java.util.List;

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


}

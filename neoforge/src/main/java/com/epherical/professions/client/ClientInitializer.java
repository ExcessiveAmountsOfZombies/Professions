package com.epherical.professions.client;


import com.epherical.professions.Constants;
import com.epherical.professions.client.gui.screen.OccupationInfoScreen;
import com.epherical.professions.client.gui.screen.OccupationMenuScreen;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import org.lwjgl.glfw.GLFW;

@EventBusSubscriber(value = Dist.CLIENT, modid = Constants.MOD_ID)
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
            mc.setScreen(new OccupationMenuScreen());
        }
    }


}

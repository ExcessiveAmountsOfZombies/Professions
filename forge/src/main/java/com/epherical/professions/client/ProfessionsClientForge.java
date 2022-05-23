package com.epherical.professions.client;

import com.epherical.professions.ProfessionsForge;
import com.epherical.professions.api.ProfessionalPlayer;
import com.epherical.professions.client.screen.OccupationScreen;
import com.epherical.professions.networking.NetworkHandler;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.ClientRegistry;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.lwjgl.glfw.GLFW;

@OnlyIn(Dist.CLIENT)
public class ProfessionsClientForge {

    private static KeyMapping occupationMenu;

    public static void initClient() {
        occupationMenu = new KeyMapping(
                "key.professions.open_occupation_menu",
                InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_R,
                "category.professions.occupation");
        ClientRegistry.registerKeyBinding(occupationMenu);
    }

    @SubscribeEvent
    public void handleInput(InputEvent.KeyInputEvent event) {
        if (occupationMenu.isDown()) {
            Minecraft client = Minecraft.getInstance();
            if (client.isLocalServer()) {
                ProfessionalPlayer player = ProfessionsForge.getInstance().getPlayerManager().getPlayer(client.player.getUUID());
                client.setScreen(new OccupationScreen<>(player.getActiveOccupations(), client, OccupationScreen::createOccupationEntries, null));
            } else {
                NetworkHandler.Client.sendOccupationPacket();
            }
        }
    }

}

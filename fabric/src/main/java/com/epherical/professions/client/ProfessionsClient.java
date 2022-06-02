package com.epherical.professions.client;

import com.epherical.professions.Constants;
import com.epherical.professions.ProfessionsFabric;
import com.epherical.professions.api.ProfessionalPlayer;
import com.epherical.professions.client.screen.OccupationScreen;
import com.epherical.professions.networking.ClientHandler;
import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.KeyMapping;
import org.lwjgl.glfw.GLFW;

@Environment(EnvType.CLIENT)
public class ProfessionsClient implements ClientModInitializer {

    private static KeyMapping occupationMenu;

    @Override
    public void onInitializeClient() {
        occupationMenu = KeyBindingHelper.registerKeyBinding(new KeyMapping(
                "key.professions.open_occupation_menu",
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_R,
                "category.professions.occupation"
        ));
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (occupationMenu.isDown()) {
                if (client.isLocalServer()) {
                    ProfessionalPlayer player = ProfessionsFabric.getInstance().getPlayerManager().getPlayer(client.player.getUUID());
                    client.setScreen(new OccupationScreen<>(player.getActiveOccupations(), client, OccupationScreen::createOccupationEntries, null));
                } else {
                    ClientHandler.sendOccupationPacket();
                }

            }
        });
        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> {
            ProfessionsFabric.getInstance().getPlayerManager().playerClientQuit(client.player.getUUID());
        });

        ClientPlayNetworking.registerGlobalReceiver(Constants.MOD_CHANNEL, ClientHandler::receivePacket);
    }
}

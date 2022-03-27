package com.epherical.professions.client;

import com.epherical.professions.ProfessionsMod;
import com.epherical.professions.api.ProfessionalPlayer;
import com.epherical.professions.client.screen.OccupationScreen;
import com.epherical.professions.networking.ClientHandler;
import com.epherical.professions.networking.PacketIdentifiers;
import com.mojang.blaze3d.platform.InputConstants;
import io.netty.buffer.Unpooled;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.KeyMapping;
import net.minecraft.network.FriendlyByteBuf;
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
                    ProfessionalPlayer player = ProfessionsMod.getInstance().getPlayerManager().getPlayer(client.player.getUUID());
                    client.setScreen(new OccupationScreen(player.getActiveOccupations()));
                } else {
                    sendOccupationPacket();
                }

            }
        });

        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> {
            ProfessionsMod.getInstance().getProfessionLoader().clearProfessions();
        });

        ClientPlayNetworking.registerGlobalReceiver(ProfessionsMod.MOD_CHANNEL, ClientHandler::receivePacket);
    }

    // todo: move this
    public static void sendOccupationPacket() {
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        buf.writeResourceLocation(PacketIdentifiers.OPEN_UI_REQUEST);
        ClientPlayNetworking.send(ProfessionsMod.MOD_CHANNEL, buf);
    }
}

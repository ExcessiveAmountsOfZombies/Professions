package com.epherical.professions.client;

import com.epherical.professions.Constants;
import com.epherical.professions.ProfessionsFabric;
import com.epherical.professions.client.editor.EditorCreator;
import com.epherical.professions.client.format.PieceRegistry;
import com.epherical.professions.client.screen.DatapackScreen;
import com.epherical.professions.client.screen.MenuScreen;
import com.epherical.professions.networking.ClientHandler;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;

@Environment(EnvType.CLIENT)
public class ProfessionsClient implements ClientModInitializer {

    public static ClientHandler clientHandler;
    public static CommonClient commonClient;

    @Override
    public void onInitializeClient() {
        clientHandler = new ClientHandler();
        commonClient = new CommonClient();
        KeyBindingHelper.registerKeyBinding(commonClient.getOccupationMenu());
        KeyBindingHelper.registerKeyBinding(commonClient.getProfessionData());
        KeyBindingHelper.registerKeyBinding(commonClient.getOpenDatapackMenu());

        PieceRegistry.init();


        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (commonClient.getOpenDatapackMenu().isDown()) {
                client.setScreen(new MenuScreen());
                //client.setScreen(createScreen(ProfessionEditor::new));
                //client.setScreen(CommonPlatform.platform.createScreen());
            }
            commonClient.openMenus(client);
        });

        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> {
            if (!client.hasSingleplayerServer()) {
                ProfessionsFabric.getInstance().getPlayerManager().playerClientQuit(client.player.getUUID());
            }
        });

        ItemTooltipCallback.EVENT.register((stack, context, lines) -> {
            Minecraft minecraft = Minecraft.getInstance();
            LocalPlayer player = minecraft.player;
            if (player == null) {
                return;
            }
            commonClient.appendToolTip(player, stack.getItem(), KeyBindingHelper.getBoundKeyOf(commonClient.getProfessionData()).getValue(),
                    KeyBindingHelper.getBoundKeyOf(commonClient.getProfessionData()).getDisplayName(), lines);
        });
        ClientPlayNetworking.registerGlobalReceiver(Constants.MOD_CHANNEL, ClientHandler::receivePacket);
    }

    public static DatapackScreen createScreen(EditorCreator<?> creator) {
        return new DatapackScreen(creator);
    }
}

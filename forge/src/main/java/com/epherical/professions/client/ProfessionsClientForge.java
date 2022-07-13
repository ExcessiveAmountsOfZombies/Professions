package com.epherical.professions.client;

import com.epherical.professions.ProfessionsForge;
import com.epherical.professions.networking.NetworkHandler;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

@OnlyIn(Dist.CLIENT)
public class ProfessionsClientForge {

    public static NetworkHandler.Client clientHandler;
    private static CommonClient commonClient;

    public static void initClient() {
        clientHandler = new NetworkHandler.Client();
        commonClient = new CommonClient();

    }

    public static void registerKeyBindings(RegisterKeyMappingsEvent event) {
        event.register(commonClient.getOccupationMenu());
        event.register(commonClient.getProfessionData());
    }

    @SubscribeEvent
    public void handleInput(InputEvent.Key event) {
        commonClient.openOccupationMenu(Minecraft.getInstance());
    }

    @SubscribeEvent
    public void onClientLogout(ClientPlayerNetworkEvent.LoggingOut event) {
        if (event.getPlayer() == null) {
            return;
        }
        Minecraft minecraft = Minecraft.getInstance();
        if (!minecraft.hasSingleplayerServer()) {
            ProfessionsForge.getInstance().getPlayerManager().playerClientQuit(event.getPlayer().getUUID());
        }
    }

    @SubscribeEvent
    public void onToolTip(ItemTooltipEvent event) {
        commonClient.appendToolTip(event.getEntity(), event.getItemStack().getItem(), commonClient.getProfessionData().getKey().getValue(),
                commonClient.getProfessionData().getKey().getDisplayName(), event.getToolTip());
    }

}

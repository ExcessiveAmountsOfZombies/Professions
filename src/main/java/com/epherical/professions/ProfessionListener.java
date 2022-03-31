package com.epherical.professions;

import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;

public class ProfessionListener {

    public void onPlayerJoin(ServerGamePacketListenerImpl handler, PacketSender sender, MinecraftServer server) {
        ProfessionsMod.getInstance().getPlayerManager().playerJoined(handler.getPlayer());
    }

    public void onPlayerLeave(ServerGamePacketListenerImpl handler, MinecraftServer server) {
        ProfessionsMod.getInstance().getPlayerManager().playerQuit(handler.getPlayer());
    }
}

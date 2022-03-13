package com.epherical.professions;

import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;

public class ProfessionListener {

    private final PlayerManager playerManager;

    public ProfessionListener(PlayerManager mod) {
        this.playerManager = mod;
    }

    public void onPlayerJoin(ServerGamePacketListenerImpl handler, PacketSender sender, MinecraftServer server) {
        playerManager.playerJoined(handler.getPlayer());
    }

    public void onPlayerLeave(ServerGamePacketListenerImpl handler, MinecraftServer server) {
        playerManager.playerQuit(handler.getPlayer());
    }
}

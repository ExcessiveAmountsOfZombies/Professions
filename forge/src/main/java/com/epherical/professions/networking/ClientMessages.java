package com.epherical.professions.networking;

import com.epherical.professions.ProfessionsForge;
import com.epherical.professions.client.screen.OccupationScreen;
import com.epherical.professions.profession.progression.Occupation;
import com.mojang.util.UUIDTypeAdapter;
import net.minecraft.client.Minecraft;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.function.Supplier;
import java.util.stream.Collectors;

public class ClientMessages {

    public static void handlePacket(NetworkHandler.RespondUI msg, Supplier<NetworkEvent.Context> ctx) {
        Minecraft client = Minecraft.getInstance();
        client.execute(() -> client.setScreen(new OccupationScreen<>(msg.occupations(), client, OccupationScreen::createOccupationEntries, null)));
    }

    public static void handlePacket(NetworkHandler.RespondInfo msg, Supplier<NetworkEvent.Context> ctx) {
        Minecraft client = Minecraft.getInstance();
        client.execute(() -> client.setScreen(new OccupationScreen<>(msg.displays(), client, OccupationScreen::createInfoEntries, CommandButtons.INFO)));
    }

    public static void handlePacket(NetworkHandler.ButtonJoin msg, Supplier<NetworkEvent.Context> ctx) {
        Minecraft minecraft = Minecraft.getInstance();
        minecraft.setScreen(new OccupationScreen<>(msg.professions(), minecraft, (screen, client, display) -> {
            return OccupationScreen.createProfessionEntries(screen, client, display, CommandButtons.JOIN);
        }, CommandButtons.JOIN));
    }

    public static void handlePacket(NetworkHandler.ButtonLeave msg, Supplier<NetworkEvent.Context> ctx) {
        Minecraft minecraft = Minecraft.getInstance();
        minecraft.setScreen(new OccupationScreen<>(msg.occupations().stream().map(Occupation::getProfession).collect(Collectors.toList()),
                minecraft, (screen, client, display) -> {
            return OccupationScreen.createProfessionEntries(screen, client, display, CommandButtons.LEAVE);
        }, CommandButtons.LEAVE));
    }

    public static void handlePacket(NetworkHandler.ButtonTop msg, Supplier<NetworkEvent.Context> ctx) {
        Minecraft minecraft = Minecraft.getInstance();
        minecraft.setScreen(new OccupationScreen<>(msg.displays(), minecraft, OccupationScreen::createTopEntries, CommandButtons.TOP));
    }

    public static void handlePacket(NetworkHandler.ButtonInfo msg, Supplier<NetworkEvent.Context> ctx) {
        Minecraft minecraft = Minecraft.getInstance();
        minecraft.setScreen(new OccupationScreen<>(msg.professions().stream().toList(), minecraft, (screen, client, display) -> {
            return OccupationScreen.createProfessionEntries(screen, client, display, CommandButtons.INFO);
        }, CommandButtons.INFO));
    }

    public static void handlePacket(NetworkHandler.SynchronizeRequest msg, Supplier<NetworkEvent.Context> ctx, SimpleChannel channel) {
        Minecraft minecraft = Minecraft.getInstance();
        if (!minecraft.isLocalServer()) {
            channel.sendToServer(new NetworkHandler.SynchronizeResponse());
        }
    }

    public static void handlePacket(NetworkHandler.SyncData msg, Supplier<NetworkEvent.Context> ctx) {
        Minecraft minecraft = Minecraft.getInstance();
        if (!minecraft.hasSingleplayerServer()) {
            ProfessionsForge.getInstance().getPlayerManager().addClientPlayer(UUIDTypeAdapter.fromString(minecraft.getUser().getUuid()), msg.occupations());
        }
    }
}

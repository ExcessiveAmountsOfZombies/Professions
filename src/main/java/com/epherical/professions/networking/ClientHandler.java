package com.epherical.professions.networking;

import com.epherical.professions.ProfessionConstants;
import com.epherical.professions.ProfessionsMod;
import com.epherical.professions.client.screen.OccupationScreen;
import com.epherical.professions.profession.Profession;
import com.epherical.professions.profession.ProfessionSerializer;
import com.epherical.professions.profession.progression.Occupation;
import io.netty.buffer.Unpooled;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Environment(EnvType.CLIENT)
public class ClientHandler {

    private static final Map<ResourceLocation, ClientPlayNetworking.PlayChannelHandler> subChannelReceivers = new HashMap<>();
    private static final Map<CommandButtons, CommandButtonHandler> buttonReceivers = new HashMap<>();
    private static final Map<CommandButtons, Runnable> buttonSenders = new HashMap<>();


    public static void receivePacket(Minecraft minecraft, ClientPacketListener listener, FriendlyByteBuf buf, PacketSender sender) {
        ResourceLocation subChannel = buf.readResourceLocation();
        ClientPlayNetworking.PlayChannelHandler handler = subChannelReceivers.getOrDefault(subChannel, null);
        if (handler != null) {
            handler.receive(minecraft, listener, buf, sender);
        }
    }

    static {
        setupSubChannels();
        setupButtonHandlers();
        setupButtonSenders();
    }

    private static void setupSubChannels() {
        subChannelReceivers.put(PacketIdentifiers.OPEN_UI_RESPONSE, (client, handler, buf, responseSender) -> {
            List<Occupation> occupations = ProfessionSerializer.fromNetwork(buf);
            client.execute(() -> client.setScreen(new OccupationScreen(occupations).addPrevious(client.screen)));
        });
        subChannelReceivers.put(PacketIdentifiers.CLICK_PROFESSION_BUTTON_RESPONSE, (client, handler, buf, responseSender) -> {
            CommandButtons button = buf.readEnum(CommandButtons.class);
            CommandButtonHandler buttonHandler = buttonReceivers.getOrDefault(button, null);
            if (buttonHandler != null) {
                buttonHandler.commandButtonClicked(client, buf);
            }
        });
    }

    private static void setupButtonSenders() {
        buttonSenders.put(CommandButtons.JOIN, () -> {
            FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
            buf.writeResourceLocation(PacketIdentifiers.CLICK_PROFESSION_BUTTON_REQUEST);
            buf.writeEnum(CommandButtons.JOIN);
            ClientPlayNetworking.send(ProfessionsMod.MOD_CHANNEL, buf);
        });
    }

    private static void setupButtonHandlers() {
        buttonReceivers.put(CommandButtons.JOIN, (minecraft, buf) -> {
            int size = buf.readVarInt();
            List<Profession> professions = new ArrayList<>();
            for (int i = 0; i < size; i++) {
                ResourceLocation serializer = buf.readResourceLocation();
                Profession profession = ProfessionConstants.PROFESSION_SERIALIZER.getOptional(serializer).orElseThrow(()
                        -> new IllegalArgumentException("Unknown profession serializer " + serializer)).fromServer(buf);
                professions.add(profession);
            }
            minecraft.execute(() -> {
                minecraft.setScreen(new OccupationScreen(professions, CommandButtons.JOIN).addPrevious(minecraft.screen));
            });
        });
    }

    public static void sendButtonPacket(CommandButtons buttons) {
        Runnable runnable = buttonSenders.getOrDefault(buttons, () -> {});
        runnable.run();
    }

    public static void attemptJoinPacket(ResourceLocation location) {
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        buf.writeResourceLocation(PacketIdentifiers.JOIN_BUTTON_REQUEST);
        buf.writeResourceLocation(location);
        ClientPlayNetworking.send(ProfessionsMod.MOD_CHANNEL, buf);
    }
    public static void sendOccupationPacket() {
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        buf.writeResourceLocation(PacketIdentifiers.OPEN_UI_REQUEST);
        ClientPlayNetworking.send(ProfessionsMod.MOD_CHANNEL, buf);
    }

    public interface CommandButtonHandler {
        void commandButtonClicked(Minecraft minecraft, FriendlyByteBuf buf);
    }
}

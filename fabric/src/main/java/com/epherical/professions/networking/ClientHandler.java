package com.epherical.professions.networking;

import com.epherical.professions.Constants;
import com.epherical.professions.client.screen.OccupationScreen;
import com.epherical.professions.profession.Profession;
import com.epherical.professions.profession.ProfessionSerializer;
import com.epherical.professions.profession.progression.Occupation;
import com.epherical.professions.util.ActionDisplay;
import com.epherical.professions.util.LevelDisplay;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
        subChannelReceivers.put(Constants.OPEN_UI_RESPONSE, (client, handler, buf, responseSender) -> {
            List<Occupation> occupations = ProfessionSerializer.fromNetwork(buf);
            client.execute(() -> client.setScreen(new OccupationScreen<>(occupations, client, OccupationScreen::createOccupationEntries, null)));
        });
        subChannelReceivers.put(Constants.INFO_BUTTON_RESPONSE, (client, handler, buf, responseSender) -> {
            int size = buf.readVarInt();
            List<ActionDisplay> displays = new ArrayList<>();
            for (int i = 0; i < size; i++) {
                displays.add(ActionDisplay.fromNetwork(buf));
            }
            client.execute(() -> client.setScreen(new OccupationScreen<>(displays, client, OccupationScreen::createInfoEntries, null)));
        });
        subChannelReceivers.put(Constants.CLICK_PROFESSION_BUTTON_RESPONSE, (client, handler, buf, responseSender) -> {
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
            buf.writeResourceLocation(Constants.CLICK_PROFESSION_BUTTON_REQUEST);
            buf.writeEnum(CommandButtons.JOIN);
            ClientPlayNetworking.send(Constants.MOD_CHANNEL, buf);
        });
        buttonSenders.put(CommandButtons.LEAVE, () -> {
            FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
            buf.writeResourceLocation(Constants.CLICK_PROFESSION_BUTTON_REQUEST);
            buf.writeEnum(CommandButtons.LEAVE);
            ClientPlayNetworking.send(Constants.MOD_CHANNEL, buf);
        });
        buttonSenders.put(CommandButtons.INFO, () -> {
            FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
            buf.writeResourceLocation(Constants.CLICK_PROFESSION_BUTTON_REQUEST);
            buf.writeEnum(CommandButtons.INFO);
            ClientPlayNetworking.send(Constants.MOD_CHANNEL, buf);
        });
        buttonSenders.put(CommandButtons.TOP, () -> {
            FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
            buf.writeResourceLocation(Constants.CLICK_PROFESSION_BUTTON_REQUEST);
            buf.writeEnum(CommandButtons.TOP);
            ClientPlayNetworking.send(Constants.MOD_CHANNEL, buf);
        });
    }

    private static void setupButtonHandlers() {
        buttonReceivers.put(CommandButtons.JOIN, (minecraft, buf) -> {
            List<Profession> professions = Profession.fromNetwork(buf);
            minecraft.execute(() -> {
                minecraft.setScreen(new OccupationScreen<>(professions, minecraft, (screen, client, display) -> {
                    return OccupationScreen.createProfessionEntries(screen, client, display, CommandButtons.JOIN);
                }, CommandButtons.JOIN));
            });
        });
        buttonReceivers.put(CommandButtons.LEAVE, (minecraft, buf) -> {
            List<Profession> professions = ProfessionSerializer.fromNetwork(buf).stream().map(Occupation::getProfession).collect(Collectors.toList());
            minecraft.execute(() -> minecraft.setScreen(new OccupationScreen<>(professions, minecraft, (screen, client, display) -> {
                return OccupationScreen.createProfessionEntries(screen, client, display, CommandButtons.LEAVE);
            }, CommandButtons.LEAVE)));
        });
        buttonReceivers.put(CommandButtons.INFO, (minecraft, buf) -> {
            List<Profession> professions = Profession.fromNetwork(buf);
            minecraft.execute(() -> {
                minecraft.setScreen(new OccupationScreen<>(professions, minecraft, (screen, client, display) -> {
                    return OccupationScreen.createProfessionEntries(screen, client, display, CommandButtons.INFO);
                }, CommandButtons.INFO));
            });
        });
        buttonReceivers.put(CommandButtons.TOP, (minecraft, buf) -> {
            int read = buf.readVarInt();
            List<LevelDisplay> displays = new ArrayList<>();
            for (int i = 0; i < read; i++) {
                displays.add(new LevelDisplay(buf.readUUID(), buf.readVarInt()));
            }
            minecraft.execute(() -> {
                minecraft.setScreen(new OccupationScreen<>(displays, minecraft, OccupationScreen::createTopEntries, CommandButtons.TOP));
            });
        });
    }

    public static void sendButtonPacket(CommandButtons buttons) {
        Runnable runnable = buttonSenders.getOrDefault(buttons, () -> {});
        runnable.run();
    }

    public static void sendOccupationPacket() {
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        buf.writeResourceLocation(Constants.OPEN_UI_REQUEST);
        ClientPlayNetworking.send(Constants.MOD_CHANNEL, buf);
    }

    public static void attemptJoinPacket(ResourceLocation location) {
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        buf.writeResourceLocation(Constants.JOIN_BUTTON_REQUEST);
        buf.writeResourceLocation(location);
        ClientPlayNetworking.send(Constants.MOD_CHANNEL, buf);
    }

    public static void attemptLeavePacket(ResourceLocation location) {
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        buf.writeResourceLocation(Constants.LEAVE_BUTTON_REQUEST);
        buf.writeResourceLocation(location);
        ClientPlayNetworking.send(Constants.MOD_CHANNEL, buf);
    }

    public static void attemptInfoPacket(ResourceLocation location) {
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        buf.writeResourceLocation(Constants.INFO_BUTTON_REQUEST);
        buf.writeResourceLocation(location);
        ClientPlayNetworking.send(Constants.MOD_CHANNEL, buf);
    }

    public interface CommandButtonHandler {
        void commandButtonClicked(Minecraft minecraft, FriendlyByteBuf buf);
    }
}

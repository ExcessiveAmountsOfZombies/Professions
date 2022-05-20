package com.epherical.professions.networking;

import com.epherical.professions.Constants;
import io.netty.buffer.Unpooled;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

public class PacketHandler {
    private static int id = 0;
    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            Constants.MOD_CHANNEL,
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals);
    //private static final Map<ResourceLocation, ClientPlayNetworking.PlayChannelHandler> subChannelReceivers = new HashMap<>();
    private static final Map<CommandButtons, CommandButtonHandler> buttonReceivers = new HashMap<>();
    private static final Map<CommandButtons, Message> buttonSenders = new HashMap<>();

    static {
        setupSubChannels();
        setupButtonSenders();
    }

    private static void setupSubChannels() {
        /*CHANNEL.sendToServer(buttonSenders.get(CommandButtons.JOIN));
        CHANNEL.registerMessage(id++, Message.class, (message, buf) -> {
            message.handle(buf);
        }, buf -> {

        });
        CHANNEL.registerMessage(id++, Message.class, (message, buf) -> )
        CHANNEL.registerMessage(id++, Message.class, Message::handle, Message::new, (message, contextSupplier) -> {

        }, Optional.of(NetworkDirection.PLAY_TO_SERVER));*/
    }
    
    private static void setupButtonSenders() {
        /*buttonSenders.put(CommandButtons.JOIN, new Message(buf -> {
            buf.writeResourceLocation(Constants.CLICK_PROFESSION_BUTTON_REQUEST);
            buf.writeEnum(CommandButtons.JOIN);
            //ClientPlayNetworking.send(Constants.MOD_CHANNEL, buf);
        }));
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
        });*/
    }

    public static class Message {
        Consumer<FriendlyByteBuf> encode;

        public Message(Consumer<FriendlyByteBuf> encoder) {

        }

        public Message(FriendlyByteBuf decode) {

        }

        public void handle(FriendlyByteBuf buf) {
            encode.accept(buf);
        }

        /*public Consumer<FriendlyByteBuf> getDecode() {
            return decode;
        }*/

        public Consumer<FriendlyByteBuf> getEncode() {
            return encode;
        }
    }

    public interface CommandButtonHandler {
        void commandButtonClicked(Minecraft minecraft, FriendlyByteBuf buf);
    }
}

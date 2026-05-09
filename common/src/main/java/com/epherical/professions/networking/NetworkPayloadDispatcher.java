package com.epherical.professions.networking;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;

import java.util.Objects;

public final class NetworkPayloadDispatcher {

    private static PayloadSender payloadSender = (player, payload) -> {};
    private static ServerboundPayloadSender serverboundPayloadSender = payload -> {};

    public static void setPayloadSender(PayloadSender payloadSender) {
        NetworkPayloadDispatcher.payloadSender = Objects.requireNonNull(payloadSender);
    }

    public static void sendToPlayer(ServerPlayer player, CustomPacketPayload payload) {
        payloadSender.sendToPlayer(player, payload);
    }

    public static void setServerboundPayloadSender(ServerboundPayloadSender serverboundPayloadSender) {
        NetworkPayloadDispatcher.serverboundPayloadSender = Objects.requireNonNull(serverboundPayloadSender);
    }

    public static void sendToServer(CustomPacketPayload payload) {
        serverboundPayloadSender.sendToServer(payload);
    }

    @FunctionalInterface
    public interface PayloadSender {
        void sendToPlayer(ServerPlayer player, CustomPacketPayload payload);
    }

    @FunctionalInterface
    public interface ServerboundPayloadSender {
        void sendToServer(CustomPacketPayload payload);
    }
}

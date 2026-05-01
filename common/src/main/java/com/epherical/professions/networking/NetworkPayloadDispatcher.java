package com.epherical.professions.networking;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;

import java.util.Objects;

public final class NetworkPayloadDispatcher {

    private static PayloadSender payloadSender = (player, payload) -> {};

    private NetworkPayloadDispatcher() {
    }

    public static void setPayloadSender(PayloadSender payloadSender) {
        NetworkPayloadDispatcher.payloadSender = Objects.requireNonNull(payloadSender);
    }

    public static void sendToPlayer(ServerPlayer player, CustomPacketPayload payload) {
        payloadSender.sendToPlayer(player, payload);
    }

    @FunctionalInterface
    public interface PayloadSender {
        void sendToPlayer(ServerPlayer player, CustomPacketPayload payload);
    }
}

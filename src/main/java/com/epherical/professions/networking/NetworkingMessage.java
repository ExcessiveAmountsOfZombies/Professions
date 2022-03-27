package com.epherical.professions.networking;

import net.minecraft.network.FriendlyByteBuf;

public interface NetworkingMessage {
    void onRecievedMessage(FriendlyByteBuf buf);
}

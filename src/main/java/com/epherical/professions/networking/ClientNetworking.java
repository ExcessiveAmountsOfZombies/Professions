package com.epherical.professions.networking;

import net.minecraft.resources.ResourceLocation;

public interface ClientNetworking {

    void attemptJoinPacket(ResourceLocation professionKey);

    void attemptLeavePacket(ResourceLocation professionKey);

    void attemptInfoPacket(ResourceLocation professionKey);

    void sendOccupationPacket();
}

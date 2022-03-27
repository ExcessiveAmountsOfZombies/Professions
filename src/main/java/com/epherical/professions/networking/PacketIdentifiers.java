package com.epherical.professions.networking;

import com.epherical.professions.ProfessionsMod;
import io.netty.buffer.Unpooled;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;


public class PacketIdentifiers {
    public static final ResourceLocation OPEN_UI_REQUEST = new ResourceLocation(ProfessionsMod.MOD_ID, "o_ui");
    public static final ResourceLocation OPEN_UI_RESPONSE = new ResourceLocation(ProfessionsMod.MOD_ID, "r_ui");
    public static final ResourceLocation CLICK_PROFESSION_BUTTON_REQUEST = new ResourceLocation(ProfessionsMod.MOD_ID, "cl_pb");
    public static final ResourceLocation CLICK_PROFESSION_BUTTON_RESPONSE = new ResourceLocation(ProfessionsMod.MOD_ID, "re_pb");
    public static final ResourceLocation JOIN_BUTTON_REQUEST = new ResourceLocation(ProfessionsMod.MOD_ID, "jo_prf");


    @Environment(EnvType.CLIENT)
    public static void clickProfessionButton(ProfessionButtons buttons) {
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        buf.writeResourceLocation(PacketIdentifiers.CLICK_PROFESSION_BUTTON_REQUEST);
        buf.writeEnum(buttons);
        ClientPlayNetworking.send(ProfessionsMod.MOD_CHANNEL, buf);
    }

    public static void attemptJoin(ResourceLocation location) {
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        buf.writeResourceLocation(JOIN_BUTTON_REQUEST);
        buf.writeResourceLocation(location);
        ClientPlayNetworking.send(ProfessionsMod.MOD_CHANNEL, buf);
    }



}

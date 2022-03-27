package com.epherical.professions.networking;

import com.epherical.professions.ProfessionsMod;
import net.minecraft.resources.ResourceLocation;


public class PacketIdentifiers {
    public static final ResourceLocation OPEN_UI_REQUEST = new ResourceLocation(ProfessionsMod.MOD_ID, "o_ui");
    public static final ResourceLocation OPEN_UI_RESPONSE = new ResourceLocation(ProfessionsMod.MOD_ID, "r_ui");
    public static final ResourceLocation CLICK_PROFESSION_BUTTON_REQUEST = new ResourceLocation(ProfessionsMod.MOD_ID, "cl_pb");
    public static final ResourceLocation CLICK_PROFESSION_BUTTON_RESPONSE = new ResourceLocation(ProfessionsMod.MOD_ID, "re_pb");
    public static final ResourceLocation JOIN_BUTTON_REQUEST = new ResourceLocation(ProfessionsMod.MOD_ID, "jo_prf");



}

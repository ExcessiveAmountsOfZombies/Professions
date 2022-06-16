package com.epherical.professions;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;

public class Constants {
    public static final String MOD_ID = "professions";
    public static final ResourceLocation MOD_CHANNEL = new ResourceLocation(MOD_ID, "data");

    public static final ResourceLocation OPEN_UI_REQUEST = new ResourceLocation(MOD_ID, "o_ui");
    public static final ResourceLocation OPEN_UI_RESPONSE = new ResourceLocation(MOD_ID, "r_ui");
    public static final ResourceLocation CLICK_PROFESSION_BUTTON_REQUEST = new ResourceLocation(MOD_ID, "cl_pb");
    public static final ResourceLocation CLICK_PROFESSION_BUTTON_RESPONSE = new ResourceLocation(MOD_ID, "re_pb");
    public static final ResourceLocation JOIN_BUTTON_REQUEST = new ResourceLocation(MOD_ID, "jo_prf");
    public static final ResourceLocation LEAVE_BUTTON_REQUEST = new ResourceLocation(MOD_ID, "le_prf");
    public static final ResourceLocation INFO_BUTTON_REQUEST = new ResourceLocation(MOD_ID, "inf_prf");
    public static final ResourceLocation INFO_BUTTON_RESPONSE = new ResourceLocation(MOD_ID, "ret_inf_prf");
    public static final ResourceLocation SYNCHRONIZE_REQUEST = new ResourceLocation(MOD_ID, "sync_req");
    public static final ResourceLocation SYNCHRONIZE_RESPONSE = new ResourceLocation(MOD_ID, "sync_rep");
    public static final ResourceLocation SYNCHRONIZE_DATA = new ResourceLocation(MOD_ID, "sync_data");

    public static LootItemConditionType UNLOCK_CONDITION;

    public static ResourceLocation modID(String name) {
        return new ResourceLocation(MOD_ID, name);
    }
}

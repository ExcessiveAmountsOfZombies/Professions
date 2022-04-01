package com.epherical.professions;

import com.epherical.professions.profession.Profession;
import com.epherical.professions.profession.ProfessionSerializer;
import com.epherical.professions.profession.action.ActionType;
import com.epherical.professions.profession.conditions.ActionConditionType;
import com.epherical.professions.profession.rewards.RewardType;
import com.mojang.serialization.Lifecycle;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

import static com.epherical.professions.ProfessionsMod.modID;

public class ProfessionConstants {

    public static final ResourceLocation OPEN_UI_REQUEST = new ResourceLocation(ProfessionsMod.MOD_ID, "o_ui");
    public static final ResourceLocation OPEN_UI_RESPONSE = new ResourceLocation(ProfessionsMod.MOD_ID, "r_ui");
    public static final ResourceLocation CLICK_PROFESSION_BUTTON_REQUEST = new ResourceLocation(ProfessionsMod.MOD_ID, "cl_pb");
    public static final ResourceLocation CLICK_PROFESSION_BUTTON_RESPONSE = new ResourceLocation(ProfessionsMod.MOD_ID, "re_pb");
    public static final ResourceLocation JOIN_BUTTON_REQUEST = new ResourceLocation(ProfessionsMod.MOD_ID, "jo_prf");
    public static final ResourceLocation LEAVE_BUTTON_REQUEST = new ResourceLocation(ProfessionsMod.MOD_ID, "le_prf");
    public static final ResourceLocation INFO_BUTTON_REQUEST = new ResourceLocation(ProfessionsMod.MOD_ID, "inf_prf");
    public static final ResourceLocation INFO_BUTTON_RESPONSE = new ResourceLocation(ProfessionsMod.MOD_ID, "ret_inf_prf");

    public static final ResourceKey<Registry<ProfessionSerializer<?>>> PROFESSION_TYPE_KEY = ResourceKey.createRegistryKey(modID("professions/occupation_type"));
    public static final ResourceKey<Registry<ActionType>> ACTION_TYPE_KEY = ResourceKey.createRegistryKey(modID("professions/actions"));
    public static final ResourceKey<Registry<ActionConditionType>> ACTION_CONDITION_KEY = ResourceKey.createRegistryKey(modID("professions/conditions"));
    public static final ResourceKey<Registry<RewardType>> REWARD_KEY = ResourceKey.createRegistryKey(modID("professions/rewards"));

    public static final Registry<ProfessionSerializer<? extends Profession>> PROFESSION_SERIALIZER = FabricRegistryBuilder.from(new MappedRegistry<>(PROFESSION_TYPE_KEY,
            Lifecycle.experimental(), null)).buildAndRegister();
    public static final Registry<ActionType> ACTION_TYPE = FabricRegistryBuilder.from(new MappedRegistry<>(ACTION_TYPE_KEY,
            Lifecycle.experimental(), null)).buildAndRegister();
    public static final Registry<ActionConditionType> ACTION_CONDITION_TYPE = FabricRegistryBuilder.from(new MappedRegistry<>(ACTION_CONDITION_KEY,
            Lifecycle.experimental(), null)).buildAndRegister();
    public static final Registry<RewardType> REWARDS = FabricRegistryBuilder.from(new MappedRegistry<>(REWARD_KEY,
            Lifecycle.experimental(), null)).buildAndRegister();
}

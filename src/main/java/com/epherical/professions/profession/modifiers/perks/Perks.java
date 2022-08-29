package com.epherical.professions.profession.modifiers.perks;

import com.epherical.professions.RegistryConstants;
import com.epherical.professions.profession.modifiers.perks.builtin.PermissionPerk;
import com.epherical.professions.profession.modifiers.perks.builtin.ScalingAttributePerk;
import com.epherical.professions.profession.modifiers.perks.builtin.SingleAttributePerk;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.GsonAdapterFactory;
import net.minecraft.world.level.storage.loot.Serializer;

import static com.epherical.professions.Constants.modID;

public class Perks {

    public static final PerkType PERMISSION_PERK_TYPE = register(modID("permission"), new PermissionPerk.PerkSerializer());
    public static final PerkType SINGLE_ATTRIBUTE_PERK = register(modID("increase_attribute"), new SingleAttributePerk.PerkSerializer());
    public static final PerkType SCALING_ATTRIBUTE_PERK = register(modID("scaling_attribute"), new ScalingAttributePerk.PerkSerializer());

    public static Object createGsonAdapter() {
        return GsonAdapterFactory.builder(RegistryConstants.PERK_TYPE, "perk", "perk", Perk::getType).build();
    }

    public static PerkType register(ResourceLocation location, Serializer<? extends Perk> serializer) {
        return Registry.register(RegistryConstants.PERK_TYPE, location, new PerkType(serializer));
    }
}

package com.epherical.professions.profession.modifiers.perks;

import com.epherical.professions.RegistryConstants;
import com.epherical.professions.profession.modifiers.perks.builtin.PermissionPerk;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.GsonAdapterFactory;
import net.minecraft.world.level.storage.loot.Serializer;

import static com.epherical.professions.Constants.modID;

public class Perks {

    public static final PerkType PERMISSION_PERK_TYPE = register(modID("permission"), new PermissionPerk.PerkSerializer());


    public static Object createGsonAdapter() {
        return GsonAdapterFactory.builder(RegistryConstants.PERK_TYPE, "perk", "perk", Perk::getType).build();
    }

    public static PerkType register(ResourceLocation location, Serializer<? extends Perk> serializer) {
        return Registry.register(RegistryConstants.PERK_TYPE, location, new PerkType(serializer));
    }
}

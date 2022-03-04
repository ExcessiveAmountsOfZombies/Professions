package com.epherical.professions.profession;

import com.epherical.professions.ProfessionsMod;
import com.google.gson.JsonObject;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;

public interface ProfessionSerializer<T extends Profession> {


    T deserialize(ResourceLocation id, JsonObject profession);

    static <S extends ProfessionSerializer<T>, T extends Profession> S registerSerializer(ResourceLocation id, S serializer) {
        return Registry.register(ProfessionsMod.PROFESSION_SERIALIZER, id, serializer);
    }

}

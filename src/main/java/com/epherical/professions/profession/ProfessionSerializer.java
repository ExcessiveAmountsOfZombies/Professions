package com.epherical.professions.profession;

import com.epherical.professions.ProfessionConstants;
import com.epherical.professions.ProfessionsMod;
import com.epherical.professions.profession.progression.Occupation;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonSerializer;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

public interface ProfessionSerializer<T extends Profession> extends JsonDeserializer<T>, JsonSerializer<T> {
    ProfessionSerializer<Profession> DEFAULT_PROFESSION = registerSerializer(ProfessionsMod.modID("default"), new Profession.Serializer());

    T fromServer(FriendlyByteBuf buf);

    /**
     * Servers that don't enforce players having the same mods should serialize to DEFAULT_PROFESSION so no errors occur on the client.
     */
    void toClient(FriendlyByteBuf buf, T profession);

    //T deserialize(ResourceLocation id, JsonObject profession);

    Class<T> getType();

    static <S extends ProfessionSerializer<T>, T extends Profession> S registerSerializer(ResourceLocation id, S serializer) {
        return Registry.register(ProfessionConstants.PROFESSION_SERIALIZER, id, serializer);
    }

}

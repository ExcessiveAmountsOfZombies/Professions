package com.epherical.professions.profession;

import com.epherical.professions.ProfessionConstants;
import com.epherical.professions.ProfessionsMod;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonSerializer;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;

public interface ProfessionSerializer<T extends Profession> extends JsonDeserializer<T>, JsonSerializer<T> {
    ProfessionSerializer<Profession> DEFAULT_PROFESSION = registerSerializer(ProfessionsMod.modID("default"), new Profession.Serializer());

    // Potential client integration in the future? This would be a case of not when they log in, but when they push
    // the appropriate keybind, and it would send the profession data and store it until they leave.
    /*T fromServer(ResourceLocation id, FriendlyByteBuf buf);

    void toClient(FriendlyByteBuf buf, T profession);*/

    //T deserialize(ResourceLocation id, JsonObject profession);

    Class<T> getType();

    static <S extends ProfessionSerializer<T>, T extends Profession> S registerSerializer(ResourceLocation id, S serializer) {
        return Registry.register(ProfessionConstants.PROFESSION_SERIALIZER, id, serializer);
    }

}

package com.epherical.professions.profession;

import com.epherical.professions.Constants;
import com.epherical.professions.RegistryConstants;
import com.epherical.professions.profession.progression.Occupation;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonSerializer;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

public interface ProfessionSerializer<T extends Profession> extends JsonDeserializer<T>, JsonSerializer<T> {
    ProfessionSerializer<Profession> DEFAULT_PROFESSION = registerSerializer(Constants.modID("default"), new Profession.Serializer());

    T fromServer(FriendlyByteBuf buf);

    /**
     * Servers that don't enforce players having the same mods should serialize to DEFAULT_PROFESSION so no errors occur on the client.
     */
    void toClient(FriendlyByteBuf buf, T profession);

    //T deserialize(ResourceLocation id, JsonObject profession);

    Class<T> getType();

    static <S extends ProfessionSerializer<T>, T extends Profession> S registerSerializer(ResourceLocation id, S serializer) {
        return Registry.register(RegistryConstants.PROFESSION_SERIALIZER, id, serializer);
    }

    static List<Occupation> fromNetwork(FriendlyByteBuf buf) {
        int size = buf.readVarInt();
        List<Occupation> occupations = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            ResourceLocation serializer = buf.readResourceLocation();
            Profession profession = RegistryConstants.PROFESSION_SERIALIZER.getOptional(serializer).orElseThrow(() -> {
                return new IllegalArgumentException("Unknown profession serializer " + serializer);
            }).fromServer(buf);
            int level = buf.readVarInt();
            double exp = buf.readDouble();
            int maxExp = buf.readVarInt();
            Occupation occupation = new Occupation(profession, exp, maxExp, level);
            occupations.add(occupation);
        }
        return occupations;
    }

}

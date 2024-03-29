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

public interface ProfessionSerializer<T extends Profession, V extends ProfessionBuilder> extends JsonDeserializer<V>, JsonSerializer<T> {
    @Deprecated
    ProfessionSerializer<Profession, ProfessionBuilder> DEFAULT_PROFESSION = registerSerializer(Constants.modID("default"), new Profession.Serializer());
    ProfessionSerializer<Profession, ProfessionBuilder> DEFAULT_PROFESSION_V2 = registerSerializer(Constants.modID("default_v2"), new Profession.SerializerV2());

    T fromServer(FriendlyByteBuf buf);

    /**
     * Servers that don't enforce players having the same mods should serialize to DEFAULT_PROFESSION so no errors occur on the client.
     */
    void toClient(FriendlyByteBuf buf, T profession, boolean sendUnlocks);

    //T deserialize(ResourceLocation id, JsonObject profession);

    Class<V> getBuilderType();

    Class<T> getType();

    static <S extends ProfessionSerializer<T, V>, T extends Profession, V extends ProfessionBuilder> S registerSerializer(ResourceLocation id, S serializer) {
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

package com.epherical.professions.profession;

import com.epherical.professions.Constants;
import com.epherical.professions.RegistryConstants;
import com.epherical.professions.profession.progression.Occupation;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonSerializer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.IForgeRegistryEntry;
import net.minecraftforge.registries.RegistryObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public interface ProfessionSerializer<T extends Profession> extends JsonDeserializer<T>, JsonSerializer<T>, IForgeRegistryEntry<ProfessionSerializer<?>> {
    DeferredRegister<ProfessionSerializer<?>> SERIALIZERS = DeferredRegister.create(RegistryConstants.PROFESSION_SERIALIZER, Constants.MOD_ID);
    RegistryObject<ProfessionSerializer<Profession>> DEFAULT_PROFESSION = SERIALIZERS.register("default", Profession.Serializer::new);

    T fromServer(FriendlyByteBuf buf);

    /**
     * Servers that don't enforce players having the same mods should serialize to DEFAULT_PROFESSION so no errors occur on the client.
     */
    void toClient(FriendlyByteBuf buf, T profession);

    //T deserialize(ResourceLocation id, JsonObject profession);

    Class<T> getType();

    /*static <S extends ProfessionSerializer<T>, T extends Profession> S registerSerializer(ResourceLocation id, S serializer) {
        return Registry.register(RegistryConstants.PROFESSION_SERIALIZER, id, serializer);
    }*/

    static List<Occupation> fromNetwork(FriendlyByteBuf buf) {
        int size = buf.readVarInt();
        List<Occupation> occupations = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            ResourceLocation serializer = buf.readResourceLocation();
            Profession profession = Objects.requireNonNull(RegistryConstants.PROFESSION_SERIALIZER.getValue(serializer)).fromServer(buf);
            int level = buf.readVarInt();
            double exp = buf.readDouble();
            int maxExp = buf.readVarInt();
            Occupation occupation = new Occupation(profession, exp, maxExp, level);
            occupations.add(occupation);
        }
        return occupations;
    }

}

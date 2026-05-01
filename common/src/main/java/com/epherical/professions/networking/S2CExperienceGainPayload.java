package com.epherical.professions.networking;

import com.epherical.professions.ProfessionsCommon;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public record S2CExperienceGainPayload(String professionName, double experienceGained) implements CustomPacketPayload {

    public static final Type<S2CExperienceGainPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(ProfessionsCommon.MOD_ID, "experience_gain"));

    public static final StreamCodec<RegistryFriendlyByteBuf, S2CExperienceGainPayload> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.STRING_UTF8, S2CExperienceGainPayload::professionName,
                    ByteBufCodecs.DOUBLE, S2CExperienceGainPayload::experienceGained,
                    S2CExperienceGainPayload::new
            );

    @Override
    @NotNull
    public Type<S2CExperienceGainPayload> type() {
        return TYPE;
    }
}

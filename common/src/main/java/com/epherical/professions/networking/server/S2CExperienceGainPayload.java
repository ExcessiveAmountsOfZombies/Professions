package com.epherical.professions.networking.server;

import com.epherical.professions.ProfessionsCommon;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;

public record S2CExperienceGainPayload(Identifier professionId, double experienceGained) implements CustomPacketPayload {

    public static final Type<S2CExperienceGainPayload> TYPE =
            new Type<>(Identifier.fromNamespaceAndPath(ProfessionsCommon.MOD_ID, "experience_gain"));

    public static final StreamCodec<RegistryFriendlyByteBuf, S2CExperienceGainPayload> STREAM_CODEC =
            StreamCodec.composite(
                    Identifier.STREAM_CODEC, S2CExperienceGainPayload::professionId,
                    net.minecraft.network.codec.ByteBufCodecs.DOUBLE, S2CExperienceGainPayload::experienceGained,
                    S2CExperienceGainPayload::new
            );

    @Override
    @NotNull
    public Type<S2CExperienceGainPayload> type() {
        return TYPE;
    }
}

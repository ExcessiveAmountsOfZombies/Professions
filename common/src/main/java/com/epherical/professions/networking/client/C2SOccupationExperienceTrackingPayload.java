package com.epherical.professions.networking.client;

import com.epherical.professions.ProfessionsCommon;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public record C2SOccupationExperienceTrackingPayload(Identifier professionId, boolean trackingEnabled) implements CustomPacketPayload {

    public static final Type<C2SOccupationExperienceTrackingPayload> TYPE =
            new Type<>(Identifier.fromNamespaceAndPath(ProfessionsCommon.MOD_ID, "occupation_experience_tracking"));

    public static final StreamCodec<RegistryFriendlyByteBuf, C2SOccupationExperienceTrackingPayload> STREAM_CODEC =
            StreamCodec.composite(
                    Identifier.STREAM_CODEC, C2SOccupationExperienceTrackingPayload::professionId,
                    ByteBufCodecs.BOOL, C2SOccupationExperienceTrackingPayload::trackingEnabled,
                    C2SOccupationExperienceTrackingPayload::new
            );

    public C2SOccupationExperienceTrackingPayload {
        professionId = Objects.requireNonNull(professionId);
    }

    @Override
    public @NotNull Type<C2SOccupationExperienceTrackingPayload> type() {
        return TYPE;
    }
}

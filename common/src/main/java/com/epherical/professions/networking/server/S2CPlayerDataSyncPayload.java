package com.epherical.professions.networking.server;

import com.epherical.professions.ProfessionsCommon;
import com.epherical.professions.model.Occupation;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public record S2CPlayerDataSyncPayload(UUID playerId, List<Occupation> occupations,
        Optional<ResourceLocation> categoryId) implements CustomPacketPayload {

    public static final Type<S2CPlayerDataSyncPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(ProfessionsCommon.MOD_ID, "player_data_sync"));

    private static final StreamCodec<RegistryFriendlyByteBuf, List<Occupation>> OCCUPATIONS_CODEC =
            ByteBufCodecs.fromCodecWithRegistries(Occupation.CODEC.listOf());

    public static final StreamCodec<RegistryFriendlyByteBuf, S2CPlayerDataSyncPayload> STREAM_CODEC =
            StreamCodec.composite(
                    UUIDUtil.STREAM_CODEC, S2CPlayerDataSyncPayload::playerId,
                    OCCUPATIONS_CODEC, S2CPlayerDataSyncPayload::occupations,
                    ByteBufCodecs.optional(ResourceLocation.STREAM_CODEC), S2CPlayerDataSyncPayload::categoryId,
                    S2CPlayerDataSyncPayload::new
            );

    @Override
    public @NotNull Type<S2CPlayerDataSyncPayload> type() {
        return TYPE;
    }
}

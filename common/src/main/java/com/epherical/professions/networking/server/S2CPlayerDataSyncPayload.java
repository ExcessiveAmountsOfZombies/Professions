package com.epherical.professions.networking.server;

import com.epherical.professions.ProfessionsCommon;
import com.epherical.professions.model.Occupation;
import com.epherical.professions.model.actions.Action;
import com.epherical.professions.model.perks.Perk;
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
        Optional<ResourceLocation> categoryId, List<Action<?>> actions, List<Perk> perks) implements CustomPacketPayload {

    public static final Type<S2CPlayerDataSyncPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(ProfessionsCommon.MOD_ID, "player_data_sync"));

    private static final StreamCodec<RegistryFriendlyByteBuf, List<Occupation>> OCCUPATIONS_CODEC =
            ByteBufCodecs.fromCodecWithRegistries(Occupation.CODEC.listOf());
    private static final StreamCodec<RegistryFriendlyByteBuf, List<Action<?>>> ACTIONS_CODEC =
            ByteBufCodecs.fromCodecWithRegistries(Action.TYPED_CODEC.listOf());
    private static final StreamCodec<RegistryFriendlyByteBuf, List<Perk>> PERKS_CODEC =
            ByteBufCodecs.fromCodecWithRegistries(Perk.TYPED_CODEC_WITH_ID.listOf());

    public static final StreamCodec<RegistryFriendlyByteBuf, S2CPlayerDataSyncPayload> STREAM_CODEC =
            StreamCodec.composite(
                    UUIDUtil.STREAM_CODEC, S2CPlayerDataSyncPayload::playerId,
                    OCCUPATIONS_CODEC, S2CPlayerDataSyncPayload::occupations,
                    ByteBufCodecs.optional(ResourceLocation.STREAM_CODEC), S2CPlayerDataSyncPayload::categoryId,
                    ACTIONS_CODEC, S2CPlayerDataSyncPayload::actions,
                    PERKS_CODEC, S2CPlayerDataSyncPayload::perks,
                    S2CPlayerDataSyncPayload::new
            );

    @Override
    public @NotNull Type<S2CPlayerDataSyncPayload> type() {
        return TYPE;
    }
}

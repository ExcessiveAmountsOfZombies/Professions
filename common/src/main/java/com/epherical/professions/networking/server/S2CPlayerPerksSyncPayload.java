package com.epherical.professions.networking.server;

import com.epherical.professions.ProfessionsCommon;
import com.epherical.professions.model.perks.Perk;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;

public record S2CPlayerPerksSyncPayload(UUID playerId, List<Perk> perks) implements CustomPacketPayload {

    public static final Type<S2CPlayerPerksSyncPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(ProfessionsCommon.MOD_ID, "player_perks_sync"));

    private static final StreamCodec<RegistryFriendlyByteBuf, List<Perk>> PERKS_CODEC =
            ByteBufCodecs.fromCodecWithRegistries(Perk.TYPED_CODEC_WITH_ID.listOf());

    public static final StreamCodec<RegistryFriendlyByteBuf, S2CPlayerPerksSyncPayload> STREAM_CODEC =
            StreamCodec.composite(
                    UUIDUtil.STREAM_CODEC, S2CPlayerPerksSyncPayload::playerId,
                    PERKS_CODEC, S2CPlayerPerksSyncPayload::perks,
                    S2CPlayerPerksSyncPayload::new
            );

    @Override
    public @NotNull Type<S2CPlayerPerksSyncPayload> type() {
        return TYPE;
    }
}

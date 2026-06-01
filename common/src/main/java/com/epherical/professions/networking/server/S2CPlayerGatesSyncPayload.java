package com.epherical.professions.networking.server;

import com.epherical.professions.ProfessionsCommon;
import com.epherical.professions.api.actions.Gate;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;

public record S2CPlayerGatesSyncPayload(UUID playerId, List<Gate<?>> gates) implements CustomPacketPayload {

    public static final Type<S2CPlayerGatesSyncPayload> TYPE =
            new Type<>(Identifier.fromNamespaceAndPath(ProfessionsCommon.MOD_ID, "player_gates_sync"));

    private static final StreamCodec<RegistryFriendlyByteBuf, List<Gate<?>>> GATES_CODEC =
            ByteBufCodecs.fromCodecWithRegistries(Gate.TYPED_CODEC.listOf());

    public static final StreamCodec<RegistryFriendlyByteBuf, S2CPlayerGatesSyncPayload> STREAM_CODEC =
            StreamCodec.composite(
                    UUIDUtil.STREAM_CODEC, S2CPlayerGatesSyncPayload::playerId,
                    GATES_CODEC, S2CPlayerGatesSyncPayload::gates,
                    S2CPlayerGatesSyncPayload::new
            );

    @Override
    public @NotNull Type<S2CPlayerGatesSyncPayload> type() {
        return TYPE;
    }
}

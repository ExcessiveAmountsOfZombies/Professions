package com.epherical.professions.networking.server;

import com.epherical.professions.ProfessionsCommon;
import com.epherical.professions.model.actions.Action;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;

public record S2CPlayerActionsSyncPayload(UUID playerId, List<Action<?>> actions) implements CustomPacketPayload {

    public static final Type<S2CPlayerActionsSyncPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(ProfessionsCommon.MOD_ID, "player_actions_sync"));

    private static final StreamCodec<RegistryFriendlyByteBuf, List<Action<?>>> ACTIONS_CODEC =
            ByteBufCodecs.fromCodecWithRegistries(Action.TYPED_CODEC.listOf());

    public static final StreamCodec<RegistryFriendlyByteBuf, S2CPlayerActionsSyncPayload> STREAM_CODEC =
            StreamCodec.composite(
                    UUIDUtil.STREAM_CODEC, S2CPlayerActionsSyncPayload::playerId,
                    ACTIONS_CODEC, S2CPlayerActionsSyncPayload::actions,
                    S2CPlayerActionsSyncPayload::new
            );

    @Override
    public @NotNull Type<S2CPlayerActionsSyncPayload> type() {
        return TYPE;
    }
}

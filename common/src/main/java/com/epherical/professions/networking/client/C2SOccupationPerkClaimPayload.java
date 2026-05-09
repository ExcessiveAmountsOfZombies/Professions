package com.epherical.professions.networking.client;

import com.epherical.professions.ProfessionsCommon;
import com.mojang.serialization.Codec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

public record C2SOccupationPerkClaimPayload(ResourceLocation professionId, Set<ResourceLocation> claimedPerks) implements CustomPacketPayload {

    public static final Type<C2SOccupationPerkClaimPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(ProfessionsCommon.MOD_ID, "occupation_perk_claim"));

    private static final Codec<Set<ResourceLocation>> PERK_IDS_CODEC =
            ResourceLocation.CODEC.listOf().xmap(LinkedHashSet::new, ArrayList::new);
    private static final StreamCodec<RegistryFriendlyByteBuf, Set<ResourceLocation>> PERK_IDS_STREAM_CODEC =
            ByteBufCodecs.fromCodecWithRegistries(PERK_IDS_CODEC);

    public static final StreamCodec<RegistryFriendlyByteBuf, C2SOccupationPerkClaimPayload> STREAM_CODEC =
            StreamCodec.composite(
                    ResourceLocation.STREAM_CODEC, C2SOccupationPerkClaimPayload::professionId,
                    PERK_IDS_STREAM_CODEC, C2SOccupationPerkClaimPayload::claimedPerks,
                    C2SOccupationPerkClaimPayload::new
            );

    public C2SOccupationPerkClaimPayload {
        Objects.requireNonNull(professionId);
        claimedPerks = Set.copyOf(Objects.requireNonNull(claimedPerks));
    }

    @Override
    public @NotNull Type<C2SOccupationPerkClaimPayload> type() {
        return TYPE;
    }
}

package com.epherical.professions.networking.server;

import com.epherical.professions.ProfessionsCommon;
import com.epherical.professions.core.ProfessionCategory;
import com.mojang.serialization.Codec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashMap;
import java.util.Map;

public record S2CCategorySyncPayload(Map<ResourceLocation, ProfessionCategory> categories) implements CustomPacketPayload {

    public static final Type<S2CCategorySyncPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(ProfessionsCommon.MOD_ID, "category_sync"));

    private static final Codec<Map<ResourceLocation, ProfessionCategory>> CATEGORY_MAP_CODEC =
            Codec.unboundedMap(ResourceLocation.CODEC, ProfessionCategory.CODEC);

    public static final StreamCodec<RegistryFriendlyByteBuf, S2CCategorySyncPayload> STREAM_CODEC =
            ByteBufCodecs.fromCodecWithRegistries(CATEGORY_MAP_CODEC).map(S2CCategorySyncPayload::new, S2CCategorySyncPayload::categories);

    public S2CCategorySyncPayload {
        categories = Map.copyOf(new LinkedHashMap<>(categories));
    }

    @Override
    public @NotNull Type<S2CCategorySyncPayload> type() {
        return TYPE;
    }
}

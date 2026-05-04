package com.epherical.professions.networking.client;

import com.epherical.professions.ProfessionsCommon;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public record C2SCategorySelectionPayload(ResourceLocation categoryId) implements CustomPacketPayload {

    public static final Type<C2SCategorySelectionPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(ProfessionsCommon.MOD_ID, "category_selection"));

    public static final StreamCodec<RegistryFriendlyByteBuf, C2SCategorySelectionPayload> STREAM_CODEC =
            StreamCodec.composite(
                    ResourceLocation.STREAM_CODEC, C2SCategorySelectionPayload::categoryId,
                    C2SCategorySelectionPayload::new
            );

    public C2SCategorySelectionPayload {
        categoryId = Objects.requireNonNull(categoryId);
    }

    @Override
    public @NotNull Type<C2SCategorySelectionPayload> type() {
        return TYPE;
    }
}

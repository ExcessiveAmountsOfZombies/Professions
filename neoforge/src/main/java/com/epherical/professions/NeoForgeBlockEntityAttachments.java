package com.epherical.professions;

import net.minecraft.core.UUIDUtil;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.attachment.AttachmentType;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;
import java.util.function.Supplier;

public class NeoForgeBlockEntityAttachments {

    public static final Supplier<AttachmentType<UUID>> PLACED_BY_UUID = NeoForgeProfessionsMod.ATTACHMENTS_REGISTER.register(
            "placed_by_uuid",
            () -> AttachmentType.builder(() -> new UUID(0L, 0L)).serialize(UUIDUtil.CODEC).sync(UUIDUtil.STREAM_CODEC).build()
    );

    public static void init() {}

    public static void setPlacedBy(BlockEntity blockEntity, UUID playerUuid) {
        blockEntity.setData(PLACED_BY_UUID, playerUuid);
    }

    public static @Nullable UUID getPlacedBy(BlockEntity blockEntity) {
        return blockEntity.getExistingData(PLACED_BY_UUID).orElse(null);
    }
}

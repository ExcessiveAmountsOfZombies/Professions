package com.epherical.professions;

import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentSyncPredicate;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.minecraft.core.UUIDUtil;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class FabricBlockEntityAttachments {

    public static final AttachmentType<UUID> PLACED_BY_UUID = AttachmentRegistry.create(ProfessionsCommon.PLACED_BY_UUID, builder -> builder
            .persistent(UUIDUtil.CODEC)
            .syncWith(UUIDUtil.STREAM_CODEC, AttachmentSyncPredicate.all()));

    public static void init() {
    }

    public static void setPlacedBy(BlockEntity blockEntity, UUID playerUuid) {
        (blockEntity).setAttached(PLACED_BY_UUID, playerUuid);
    }

    public static @Nullable UUID getPlacedBy(BlockEntity blockEntity) {
        return blockEntity.getAttached(PLACED_BY_UUID);
    }
}

package com.epherical.professions.integration.cardinal;

import com.epherical.professions.ProfessionsFabric;
import com.epherical.professions.config.ProfessionConfig;
import dev.onyxstudios.cca.api.v3.component.Component;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.UUID;

public class PlayerOwning implements Component {

    private UUID placedBy;


    public PlayerOwning(BlockEntity blockEntity) {}

    @Override
    public void readFromNbt(CompoundTag tag) {
        if (tag.contains("professions_owner")) {
            placedBy = tag.getUUID("professions_owner");
        }
    }

    @Override
    public void writeToNbt(CompoundTag tag) {
        if (placedBy != null) {
            if (ProfessionsFabric.isStopping) {
                if (ProfessionConfig.persistBlockOwnership) {
                    tag.putUUID("professions_owner", placedBy);
                }
            } else {
                tag.putUUID("professions_owid", placedBy);
            }
        }

    }

    public boolean hasOwner() {
        return placedBy != null;
    }

    public void setPlacedBy(UUID uuid) {
        this.placedBy = uuid;
    }

    public UUID getPlacedBy() {
        return placedBy;
    }
}

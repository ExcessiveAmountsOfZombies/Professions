package com.epherical.professions.profession.rewards;

import com.epherical.professions.profession.conditions.ActionConditionType;
import com.google.common.reflect.TypeToken;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.Serializer;
import net.minecraft.world.level.storage.loot.SerializerType;
import net.minecraftforge.registries.IForgeRegistryEntry;
import org.jetbrains.annotations.Nullable;

public class RewardType extends SerializerType<Reward> implements IForgeRegistryEntry<RewardType> {

    public RewardType(Serializer<? extends Reward> serializer) {
        super(serializer);
    }

    private final TypeToken<RewardType> token = new TypeToken<>(getClass()){};
    private ResourceLocation registryName = null;

    @Override
    public RewardType setRegistryName(ResourceLocation name) {
        if (getRegistryName() != null)
            throw new IllegalStateException("Attempted to set registry name with existing registry name! New: " + name + " Old: " + getRegistryName());

        this.registryName = name;
        return this;
    }

    @Nullable
    @Override
    public ResourceLocation getRegistryName() {
        return registryName != null ? registryName : null;
    }

    @Override
    public Class<RewardType> getRegistryType() {
        return (Class<RewardType>) token.getRawType();
    }
}

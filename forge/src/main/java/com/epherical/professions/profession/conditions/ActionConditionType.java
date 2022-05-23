package com.epherical.professions.profession.conditions;

import com.google.common.reflect.TypeToken;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.Serializer;
import net.minecraft.world.level.storage.loot.SerializerType;
import net.minecraftforge.registries.IForgeRegistryEntry;
import org.jetbrains.annotations.Nullable;

public class ActionConditionType extends SerializerType<ActionCondition> implements IForgeRegistryEntry<ActionConditionType> {

    public ActionConditionType(Serializer<? extends ActionCondition> serializer) {
        super(serializer);
    }

    private final TypeToken<ActionConditionType> token = new TypeToken<>(getClass()){};
    private ResourceLocation registryName = null;

    @Override
    public ActionConditionType setRegistryName(ResourceLocation name) {
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
    public Class<ActionConditionType> getRegistryType() {
        return (Class<ActionConditionType>) token.getRawType();
    }
}

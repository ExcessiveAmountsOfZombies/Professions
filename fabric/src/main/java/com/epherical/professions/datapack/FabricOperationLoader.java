package com.epherical.professions.datapack;

import com.epherical.professions.profession.operation.ObjectOperation;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

public class FabricOperationLoader<OP extends ObjectOperation<?>, T> extends OperationLoader<OP, T> implements IdentifiableResourceReloadListener {


    public FabricOperationLoader(String dataLocation, Class<? extends OP> clazz, ResourceKey<Registry<T>> resourceKey) {
        super(dataLocation, clazz, resourceKey);
    }

    @Override
    public ResourceLocation getFabricId() {
        return new ResourceLocation("professions", getDirectory());
    }
}

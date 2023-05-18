package com.epherical.professions.datapack;

import com.epherical.professions.profession.operation.AbstractOperation;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

public class FabricOperationLoader<OP extends AbstractOperation<T>, T> extends OperationLoader<OP, T> implements IdentifiableResourceReloadListener {


    public FabricOperationLoader(String dataLocation, ResourceKey<Registry<T>> resourceKey) {
        super(dataLocation, resourceKey);
    }

    @Override
    public ResourceLocation getFabricId() {
        return new ResourceLocation("professions", getDirectory());
    }
}

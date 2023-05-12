package com.epherical.professions.datapack;

import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.minecraft.resources.ResourceLocation;


public class FabricProfLoaderV2 extends ProfessionLoaderV2 implements IdentifiableResourceReloadListener {

    public FabricProfLoaderV2() {
        super("professions/occupations2");
    }

    @Override
    public ResourceLocation getFabricId() {
        return new ResourceLocation("professions", "professions/occupations2");
    }
}

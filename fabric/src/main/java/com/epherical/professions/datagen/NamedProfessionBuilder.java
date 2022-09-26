package com.epherical.professions.datagen;

import com.epherical.professions.profession.ProfessionBuilder;
import net.minecraft.data.CachedOutput;
import net.minecraft.resources.ResourceLocation;

import java.io.IOException;
import java.nio.file.Path;

public abstract class NamedProfessionBuilder implements ProviderHelpers {

    private ProfessionBuilder builder;

    public NamedProfessionBuilder(ProfessionBuilder builder) {
        this.builder = builder;
        addData(this.builder);
    }

    public ProfessionBuilder getBuilder() {
        return builder;
    }

    public abstract void addData(ProfessionBuilder builder);

    public void generateNormal(CachedOutput cache, Path path, ResourceLocation location, boolean isForge) throws IOException {
        generate(cache, builder.build(), createNormalPath(path, location, isForge));
    }
}

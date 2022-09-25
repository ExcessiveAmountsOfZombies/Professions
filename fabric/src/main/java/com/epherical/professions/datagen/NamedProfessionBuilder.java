package com.epherical.professions.datagen;

import com.epherical.professions.profession.ProfessionBuilder;
import com.google.gson.Gson;
import net.minecraft.data.HashCache;
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

    public void generateNormal(Gson gson, HashCache cache, Path path, ResourceLocation location, boolean isForge) throws IOException {
        generate(gson, cache, builder.build(), createNormalPath(path, location, isForge));
    }
}

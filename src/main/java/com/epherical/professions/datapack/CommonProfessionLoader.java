package com.epherical.professions.datapack;

import com.epherical.professions.profession.Profession;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Set;

public interface CommonProfessionLoader {

    @Nullable
    Profession getProfession(ResourceLocation location);

    Collection<Profession> getProfessions();

    Set<ResourceLocation> getProfessionKeys();

    @Nullable
    ResourceLocation getIDFromProfession(Profession profession);

    void clearProfessions();

}

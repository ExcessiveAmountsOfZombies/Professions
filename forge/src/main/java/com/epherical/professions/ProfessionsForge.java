package com.epherical.professions;

import com.epherical.professions.profession.Profession;
import com.epherical.professions.profession.ProfessionSerializer;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.RegistryBuilder;

@Mod("professions")
public class ProfessionsForge {
    
    public ProfessionsForge() {
        RegistryBuilder<ProfessionSerializer<? extends Profession>> builder = null;
        builder.setType(ProfessionSerializer.class);
        builder.create();

    }



}

package com.epherical.professions.profession.editor;

import com.epherical.professions.profession.ProfessionBuilder;
import net.minecraft.resources.ResourceLocation;

public interface Editor {
    // TODO: add data generators for editors

    ResourceLocation getProfessionKey();

    /**
     * @param location This is the file an edit came from. missing the .json but should give an idea of what edits were applied from where.
     */
    void setLocation(ResourceLocation location);

    /**
     * Method to return the general location of where an edit was made so it can be easier to identify where an edit came from.
     * @return the name of the file minus .json
     */
    ResourceLocation getLocationOfEdit();

    void applyEdit(ProfessionBuilder builder);
}

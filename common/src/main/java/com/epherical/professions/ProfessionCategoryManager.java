package com.epherical.professions;

import com.epherical.professions.core.ProfessionCategory;
import net.minecraft.resources.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class ProfessionCategoryManager {

    private static final Logger LOGGER = LogManager.getLogger();

    private volatile Map<ResourceLocation, ProfessionCategory> categoryMap = Map.of();

    public Map<ResourceLocation,ProfessionCategory> reloadCategories(Map<ResourceLocation, ProfessionCategory> categories) {
        categoryMap = categories;
        LOGGER.info("Reloaded {} profession categories", categoryMap.size());
        return categoryMap;
    }

    public Collection<ProfessionCategory> getCategories() {
        return categoryMap.values();
    }

    public Map<ResourceLocation, ProfessionCategory> getCategoryMap() {
        return categoryMap;
    }

    public @Nullable ProfessionCategory getCategory(ResourceLocation id) {
        return categoryMap.get(id);
    }

    public @Nullable ResourceLocation getCategoryId(ProfessionCategory category) {
        for (Map.Entry<ResourceLocation, ProfessionCategory> entry : categoryMap.entrySet()) {
            if (entry.getValue().equals(category)) {
                return entry.getKey();
            }
        }
        return null;
    }
}

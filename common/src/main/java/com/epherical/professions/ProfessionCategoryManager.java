package com.epherical.professions;

import com.epherical.professions.core.ProfessionCategory;
import net.minecraft.resources.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ProfessionCategoryManager {

    private static final Logger LOGGER = LogManager.getLogger();

    private volatile Map<ResourceLocation, ProfessionCategory> categoryMap = Map.of();

    public void reloadCategories(Map<ResourceLocation, ProfessionCategory> categories) {
        categoryMap = Collections.unmodifiableMap(new LinkedHashMap<>(categories));
        LOGGER.info("Reloaded {} profession categories", categoryMap.size());
    }

    public List<ProfessionCategory> getCategories() {
        return List.copyOf(categoryMap.values());
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

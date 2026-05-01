package com.epherical.professions;

import com.epherical.professions.core.ProfessionCategory;
import net.minecraft.resources.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ProfessionCategoryManager {

    private static final Logger LOGGER = LogManager.getLogger();

    private final Map<ResourceLocation, ProfessionCategory> categoryMap = new LinkedHashMap<>();

    public synchronized void reloadCategories(Map<ResourceLocation, ProfessionCategory> categories) {
        categoryMap.clear();
        categoryMap.putAll(categories);
        LOGGER.info("Reloaded {} profession categories", categoryMap.size());
    }

    public synchronized List<ProfessionCategory> getCategories() {
        return List.copyOf(categoryMap.values());
    }

    public synchronized Map<ResourceLocation, ProfessionCategory> getCategoryMap() {
        return Map.copyOf(categoryMap);
    }

    public synchronized @Nullable ProfessionCategory getCategory(ResourceLocation id) {
        return categoryMap.get(id);
    }

    public synchronized @Nullable ResourceLocation getCategoryId(ProfessionCategory category) {
        for (Map.Entry<ResourceLocation, ProfessionCategory> entry : categoryMap.entrySet()) {
            if (entry.getValue().equals(category)) {
                return entry.getKey();
            }
        }
        return null;
    }
}

package com.epherical.professions.profession.action.builtin.entity;

import com.epherical.professions.ProfessionPlatform;
import com.epherical.professions.config.ProfessionConfig;
import com.epherical.professions.profession.ProfessionContext;
import com.epherical.professions.profession.ProfessionParameter;
import com.epherical.professions.profession.action.AbstractAction;
import com.epherical.professions.profession.conditions.ActionCondition;
import com.epherical.professions.profession.rewards.Reward;
import com.epherical.professions.profession.rewards.RewardType;
import com.epherical.professions.util.ActionDisplay;
import com.epherical.professions.util.ActionEntry;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.mojang.logging.LogUtils;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public abstract class AbstractEntityAction extends AbstractAction<EntityType<?>> {
    private static final Logger LOGGER = LogUtils.getLogger();
    protected List<ActionEntry<EntityType<?>>> entities;
    @Nullable
    protected Set<EntityType<?>> realEntities;

    protected AbstractEntityAction(ActionCondition[] conditions, Reward[] rewards, List<ActionEntry<EntityType<?>>> entities) {
        super(conditions, rewards);
        this.entities = entities;
    }

    @Override
    public boolean test(ProfessionContext professionContext) {
        Entity entity = professionContext.getPossibleParameter(ProfessionParameter.ENTITY);
        logAction(professionContext, entity != null ? entity.getType().getDescription() : Component.nullToEmpty(""));
        return entity != null && getRealEntities().contains(entity.getType());
    }

    @Override
    public boolean internalCondition(ProfessionContext context) {
        return true;
    }

    @Override
    public List<Component> displayInformation() {
        List<Component> components = new ArrayList<>();
        Map<RewardType, Component> map = getRewardInformation();
        for (EntityType<?> entity : getRealEntities()) {
            components.add((entity.getDescription().copy()).setStyle(Style.EMPTY.withColor(ProfessionConfig.descriptors))
                    .append(ProfessionPlatform.platform.displayInformation(this, map)));
        }
        return components;
    }

    @Override
    public List<ActionDisplay.Icon> clientFriendlyInformation(Component actionType) {
        List<ActionDisplay.Icon> components = new ArrayList<>();
        for (EntityType<?> entity : getRealEntities()) {
            ActionDisplay.Icon icon = new ActionDisplay.Icon(Items.ZOMBIE_HEAD, (entity.getDescription().copy())
                    .setStyle(Style.EMPTY.withColor(ProfessionConfig.descriptors)), allRewardInformation(), actionType);
            components.add(icon);
        }
        return components;
    }

    protected Set<EntityType<?>> getRealEntities() {
        if (realEntities == null) {
            realEntities = new LinkedHashSet<>();
            for (ActionEntry<EntityType<?>> entity : entities) {
                realEntities.addAll(entity.getActionValues(Registry.ENTITY_TYPE));
            }
        }
        return realEntities;
    }

    public List<ActionEntry<EntityType<?>>> getEntities() {
        return entities;
    }

    public abstract static class Builder<T extends Builder<T>> extends AbstractAction.Builder<T> {
        protected final List<ActionEntry<EntityType<?>>> entries = new ArrayList<>();

        public Builder<T> entity(EntityType<?>... entityType) {
            this.entries.add(ActionEntry.of(entityType));
            return this;
        }

        public Builder<T> entity(TagKey<EntityType<?>> type) {
            this.entries.add(ActionEntry.of(type));
            return this;
        }
    }

    public abstract static class Serializer<T extends AbstractEntityAction> extends ActionSerializer<T> {

        @Override
        public void serialize(@NotNull JsonObject json, T value, @NotNull JsonSerializationContext serializationContext) {
            super.serialize(json, value, serializationContext);
            JsonArray array = new JsonArray();
            for (ActionEntry<EntityType<?>> entity : value.entities) {
                array.addAll(entity.serialize(Registry.ENTITY_TYPE));
            }
            json.add("entities", array);
        }

        public List<ActionEntry<EntityType<?>>> deserializeEntities(JsonObject object) {
            JsonArray array = GsonHelper.getAsJsonArray(object, "entities");
            List<ActionEntry<EntityType<?>>> entities = new ArrayList<>();
            for (JsonElement element : array) {
                String entityID = element.getAsString();
                if (entityID.startsWith("#")) {
                    TagKey<EntityType<?>> key = TagKey.create(Registry.ENTITY_TYPE_REGISTRY, new ResourceLocation(entityID.substring(1)));
                    entities.add(ActionEntry.of(key));
                } else {
                    Registry.ENTITY_TYPE.getOptional(new ResourceLocation(entityID)).ifPresentOrElse(
                            entity -> entities.add(ActionEntry.of(entity)),
                            () -> LOGGER.warn("Attempted to add unknown entity {}. Was not added, but will continue processing the list.", entityID));
                }
            }
            return entities;
        }
    }

}

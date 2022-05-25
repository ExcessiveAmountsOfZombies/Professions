package com.epherical.professions.profession.action.builtin.entity;

import com.epherical.professions.config.ProfessionConfig;
import com.epherical.professions.profession.ProfessionContext;
import com.epherical.professions.profession.ProfessionParameter;
import com.epherical.professions.profession.action.AbstractAction;
import com.epherical.professions.profession.conditions.ActionCondition;
import com.epherical.professions.profession.rewards.Reward;
import com.epherical.professions.profession.rewards.RewardType;
import com.epherical.professions.profession.rewards.Rewards;
import com.epherical.professions.util.ActionEntry;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.mojang.logging.LogUtils;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class AbstractEntityAction extends AbstractAction {
    private static final Logger LOGGER = LogUtils.getLogger();
    protected List<ActionEntry<EntityType<?>>> entities;
    @Nullable
    protected List<EntityType<?>> realEntities;

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
            components.add((entity.getDescription().copy()).setStyle(Style.EMPTY.withColor(ProfessionConfig.descriptors)).append(new TranslatableComponent(" (%s%s)",
                    //map.get(Rewards.PAYMENT_REWARD),
                    map.get(Rewards.EXPERIENCE_REWARD.get()),
                    extraRewardInformation(map))));
        }
        return components;
    }

    @Override
    public List<Component> clientFriendlyInformation() {
        List<Component> components = new ArrayList<>();
        for (EntityType<?> entity : getRealEntities()) {
            components.add((entity.getDescription().copy())
                    .setStyle(Style.EMPTY
                            .withColor(ProfessionConfig.descriptors)
                            .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, allRewardInformation()))));
        }
        return components;
    }

    protected List<EntityType<?>> getRealEntities() {
        if (realEntities == null) {
            realEntities = new ArrayList<>();
            for (ActionEntry<EntityType<?>> entity : entities) {
                realEntities.addAll(entity.getActionValues(Registry.ENTITY_TYPE));
            }
        }
        return realEntities;
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

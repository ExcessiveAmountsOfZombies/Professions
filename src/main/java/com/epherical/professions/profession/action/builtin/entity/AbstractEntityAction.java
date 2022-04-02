package com.epherical.professions.profession.action.builtin.entity;

import com.epherical.professions.config.ProfessionConfig;
import com.epherical.professions.profession.ProfessionContext;
import com.epherical.professions.profession.ProfessionParameter;
import com.epherical.professions.profession.action.AbstractAction;
import com.epherical.professions.profession.conditions.ActionCondition;
import com.epherical.professions.profession.rewards.Reward;
import com.epherical.professions.profession.rewards.RewardType;
import com.epherical.professions.profession.rewards.Rewards;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class AbstractEntityAction extends AbstractAction {
    protected List<EntityType<?>> entities;

    protected AbstractEntityAction(ActionCondition[] conditions, Reward[] rewards, List<EntityType<?>> entities) {
        super(conditions, rewards);
        this.entities = entities;
    }

    @Override
    public boolean test(ProfessionContext professionContext) {
        Entity entity = professionContext.getPossibleParameter(ProfessionParameter.ENTITY);
        return entity != null && entities.contains(entity.getType());
    }

    @Override
    public List<Component> displayInformation() {
        List<Component> components = new ArrayList<>();
        Map<RewardType, Component> map = getRewardInformation();
        for (EntityType<?> entity : entities) {
            components.add((entity.getDescription().copy()).setStyle(Style.EMPTY.withColor(ProfessionConfig.descriptors)).append(new TranslatableComponent(" (%s | %s & %s)",
                    map.get(Rewards.PAYMENT_REWARD),
                    map.get(Rewards.EXPERIENCE_REWARD),
                    extraRewardInformation(map))));
        }
        return components;
    }

    @Override
    public List<Component> clientFriendlyInformation() {
        List<Component> components = new ArrayList<>();
        for (EntityType<?> entity : entities) {
            components.add((entity.getDescription().copy())
                    .setStyle(Style.EMPTY
                            .withColor(ProfessionConfig.descriptors)
                            .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, allRewardInformation()))));
        }
        return components;
    }

    public abstract static class Serializer<T extends AbstractEntityAction> extends ActionSerializer<T> {

        @Override
        public void serialize(@NotNull JsonObject json, T value, @NotNull JsonSerializationContext serializationContext) {
            super.serialize(json, value, serializationContext);
            JsonArray array = new JsonArray();
            for (EntityType<?> entityType : value.entities) {
                array.add(Registry.ENTITY_TYPE.getKey(entityType).toString());
            }
            json.add("entities", array);
        }

        public List<EntityType<?>> deserializeEntities(JsonObject object) {
            JsonArray array = GsonHelper.getAsJsonArray(object, "entities");
            List<EntityType<?>> blocks = new ArrayList<>();
            for (JsonElement element : array) {
                String blockID = element.getAsString();
                blocks.add(Registry.ENTITY_TYPE.get(new ResourceLocation(blockID)));
            }
            return blocks;
        }
    }

}

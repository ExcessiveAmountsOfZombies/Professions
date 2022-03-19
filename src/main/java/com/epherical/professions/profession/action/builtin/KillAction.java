package com.epherical.professions.profession.action.builtin;

import com.epherical.professions.profession.ProfessionContext;
import com.epherical.professions.profession.ProfessionParameter;
import com.epherical.professions.profession.action.AbstractAction;
import com.epherical.professions.profession.action.ActionType;
import com.epherical.professions.profession.action.Actions;
import com.epherical.professions.profession.conditions.ActionCondition;
import com.epherical.professions.profession.rewards.Reward;
import com.epherical.professions.profession.rewards.RewardType;
import com.epherical.professions.profession.rewards.Rewards;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
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

public class KillAction extends AbstractAction {
    protected final List<EntityType<?>> entityTypes;

    protected KillAction(ActionCondition[] conditions, Reward[] rewards, List<EntityType<?>> types) {
        super(conditions, rewards);
        this.entityTypes = types;
    }

    @Override
    public boolean test(ProfessionContext professionContext) {
        Entity entity = professionContext.getPossibleParameter(ProfessionParameter.ENTITY);
        return entity != null && entityTypes.contains(entity.getType());
    }

    @Override
    public ActionType getType() {
        return Actions.KILL_ENTITY;
    }

    @Override
    public List<Component> displayInformation() {
        List<Component> components = new ArrayList<>();
        Map<RewardType, Component> map = getRewardInformation();
        for (EntityType<?> entity : entityTypes) {
            components.add(((TranslatableComponent) entity.getDescription()).setStyle(Style.EMPTY.withColor(ChatFormatting.GRAY)).append(new TranslatableComponent(" (%s | %s & %s)",
                    map.get(Rewards.PAYMENT_REWARD),
                    map.get(Rewards.EXPERIENCE_REWARD),
                    extraRewardInformation(map))));
        }
        return components;
    }

    public static class Serializer extends ActionSerializer<KillAction> {

        @Override
        public void serialize(@NotNull JsonObject json, KillAction value, @NotNull JsonSerializationContext serializationContext) {
            super.serialize(json, value, serializationContext);
            JsonArray array = new JsonArray();
            for (EntityType<?> entityType : value.entityTypes) {
                array.add(Registry.ENTITY_TYPE.getKey(entityType).toString());
            }
            json.add("entities", array);
        }

        @Override
        public KillAction deserialize(JsonObject object, JsonDeserializationContext context, ActionCondition[] conditions, Reward[] rewards) {
            JsonArray array = GsonHelper.getAsJsonArray(object, "entities");
            List<EntityType<?>> entities = new ArrayList<>();
            for (JsonElement element : array) {
                String entityID = element.getAsString();
                entities.add(Registry.ENTITY_TYPE.get(new ResourceLocation(entityID)));
            }
            return new KillAction(conditions, rewards, entities);
        }
    }
}

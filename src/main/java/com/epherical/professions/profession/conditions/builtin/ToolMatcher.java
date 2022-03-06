package com.epherical.professions.profession.conditions.builtin;

import com.epherical.professions.profession.ProfessionContext;
import com.epherical.professions.profession.ProfessionParameter;
import com.epherical.professions.profession.conditions.ActionCondition;
import com.epherical.professions.profession.conditions.ActionConditionType;
import com.epherical.professions.profession.conditions.ActionConditions;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.Serializer;

public record ToolMatcher(ItemPredicate predicate) implements ActionCondition {

    @Override
    public ActionConditionType getType() {
        return ActionConditions.TOOL_MATCHES;
    }

    @Override
    public boolean test(ProfessionContext context) {
        ItemStack stack = context.getPossibleParameter(ProfessionParameter.TOOL);
        return stack != null && this.predicate.matches(stack);
    }

    public static Builder toolMatcher(ItemPredicate.Builder builder) {
        return () -> new ToolMatcher(builder.build());
    }

    public static class ToolSerializer implements Serializer<ToolMatcher> {

        @Override
        public void serialize(JsonObject json, ToolMatcher value, JsonSerializationContext serializationContext) {
            json.add("predicate", value.predicate.serializeToJson());
        }

        @Override
        public ToolMatcher deserialize(JsonObject json, JsonDeserializationContext serializationContext) {
            ItemPredicate predicate = ItemPredicate.fromJson(json.get("predicate"));
            return new ToolMatcher(predicate);
        }
    }
}

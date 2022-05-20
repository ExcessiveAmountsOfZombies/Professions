package com.epherical.professions.profession.conditions.builtin;

import com.epherical.professions.profession.ProfessionContext;
import com.epherical.professions.profession.ProfessionParameter;
import com.epherical.professions.profession.conditions.ActionCondition;
import com.epherical.professions.profession.conditions.ActionConditionType;
import com.epherical.professions.profession.conditions.ActionConditions;
import com.epherical.professions.util.mixins.StatePropertiesPredicateHelper;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.minecraft.advancements.critereon.StatePropertiesPredicate;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;

/**
 * Based on {@link LootItemBlockStatePropertyCondition}
 */
public class BlockStatePropertyAnyCondition extends BlockStatePropertyCondition {

    public BlockStatePropertyAnyCondition(StatePropertiesPredicate predicate) {
        super(Blocks.AIR, predicate);
    }

    @Override
    public ActionConditionType getType() {
        return ActionConditions.BLOCK_STATE_MATCHES_ANY;
    }

    @Override
    public boolean test(ProfessionContext context) {
        BlockState state = context.getPossibleParameter(ProfessionParameter.THIS_BLOCKSTATE);
        return state != null && ((StatePropertiesPredicateHelper) this.predicate).professions$anyMatches(state.getBlock().getStateDefinition(), state);
    }

    public static Builder checkProperties() {
        return new Builder();
    }

    public static class Builder implements ActionCondition.Builder {
        private StatePropertiesPredicate predicate;

        public Builder properties(StatePropertiesPredicate.Builder builder) {
            this.predicate = builder.build();
            return this;
        }

        @Override
        public ActionCondition build() {
            return new BlockStatePropertyAnyCondition(predicate);
        }
    }

    public static class Serializer implements net.minecraft.world.level.storage.loot.Serializer<BlockStatePropertyAnyCondition> {
        public void serialize(JsonObject jsonObject, BlockStatePropertyAnyCondition condition, JsonSerializationContext jsonSerializationContext) {
            jsonObject.add("properties", condition.predicate.serializeToJson());
        }

        public BlockStatePropertyAnyCondition deserialize(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
            StatePropertiesPredicate predicate = StatePropertiesPredicate.fromJson(jsonObject.get("properties"));
            return new BlockStatePropertyAnyCondition(predicate);
        }
    }
}

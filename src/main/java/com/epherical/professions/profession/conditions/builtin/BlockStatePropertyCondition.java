package com.epherical.professions.profession.conditions.builtin;

import com.epherical.professions.profession.ProfessionContext;
import com.epherical.professions.profession.ProfessionParameter;
import com.epherical.professions.profession.conditions.ActionCondition;
import com.epherical.professions.profession.conditions.ActionConditionType;
import com.epherical.professions.profession.conditions.ActionConditions;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSyntaxException;
import net.minecraft.advancements.critereon.StatePropertiesPredicate;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;


/**
 * Based on {@link LootItemBlockStatePropertyCondition}
 */
public class BlockStatePropertyCondition implements ActionCondition {
    protected final Block block;
    protected final StatePropertiesPredicate predicate;

    /**
     *
     */
    public BlockStatePropertyCondition(Block block, StatePropertiesPredicate predicate) {
        this.block = block;
        this.predicate = predicate;
    }


    @Override
    public ActionConditionType getType() {
        return ActionConditions.BLOCK_STATE_MATCHES;
    }

    @Override
    public boolean test(ProfessionContext context) {
        BlockState state = context.getPossibleParameter(ProfessionParameter.THIS_BLOCKSTATE);
        return state != null && state.is(this.block) && this.predicate.matches(state);
    }

    public static Builder checkProperties(Block block) {
        return new Builder(block);
    }

    public static class Builder implements ActionCondition.Builder {
        private final Block block;
        private StatePropertiesPredicate predicate;

        public Builder(Block block) {
            this.block = block;
        }

        public Builder properties(StatePropertiesPredicate.Builder builder) {
            this.predicate = builder.build();
            return this;
        }

        @Override
        public ActionCondition build() {
            return new BlockStatePropertyCondition(block, predicate);
        }
    }

    public static class Serializer implements net.minecraft.world.level.storage.loot.Serializer<BlockStatePropertyCondition> {
        public void serialize(JsonObject jsonObject, BlockStatePropertyCondition condition, JsonSerializationContext jsonSerializationContext) {
            jsonObject.addProperty("block", Registry.BLOCK.getKey(condition.block).toString());
            jsonObject.add("properties", condition.predicate.serializeToJson());
        }

        public BlockStatePropertyCondition deserialize(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
            ResourceLocation resourceLocation = new ResourceLocation(GsonHelper.getAsString(jsonObject, "block"));
            Block block = Registry.BLOCK.getOptional(resourceLocation).orElseThrow(() -> new IllegalArgumentException("Can't find block " + resourceLocation));
            StatePropertiesPredicate predicate = StatePropertiesPredicate.fromJson(jsonObject.get("properties"));
            predicate.checkState(block.getStateDefinition(), (string) -> {
                throw new JsonSyntaxException("Block " + block + " has no property " + string);
            });
            return new BlockStatePropertyCondition(block, predicate);
        }
    }
}

package com.epherical.professions.profession.action.builtin;

import com.epherical.professions.profession.action.AbstractAction;
import com.epherical.professions.profession.action.Action;
import com.epherical.professions.profession.action.ActionType;
import com.epherical.professions.profession.action.Actions;
import com.epherical.professions.profession.conditions.ActionCondition;
import com.epherical.professions.profession.rewards.Reward;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.block.Block;

import java.util.ArrayList;
import java.util.List;

public class BreakBlockAction extends BasicBlockAbstractAction {

    protected BreakBlockAction(ActionCondition[] conditions, Reward[] rewards, List<Block> blocks) {
        super(conditions, rewards, blocks);
    }

    @Override
    public ActionType getType() {
        return Actions.BREAK_BLOCK;
    }

    public static class Builder extends AbstractAction.Builder<BreakBlockAction.Builder> {
        private List<Block> blocks = new ArrayList<>();

        public Builder withBlock(Block block) {
            this.blocks.add(block);
            return this;
        }

        public Builder withBlocks(Block... blocks) {
            this.blocks.addAll(List.of(blocks));
            return this;
        }

        @Override
        protected Builder instance() {
            return this;
        }

        @Override
        public Action build() {
            return new BreakBlockAction(this.getConditions(), this.getRewards(), blocks);
        }
    }

    public static class Serializer extends BasicBlockAbstractAction.Serializer<BreakBlockAction> {

        @Override
        public BreakBlockAction deserialize(JsonObject object, JsonDeserializationContext context, ActionCondition[] conditions, Reward[] rewards) {
            JsonArray array = GsonHelper.getAsJsonArray(object, "blocks");
            List<Block> blocks = new ArrayList<>();
            for (JsonElement element : array) {
                String blockID = element.getAsString();
                blocks.add(Registry.BLOCK.get(new ResourceLocation(blockID)));
            }
            return new BreakBlockAction(conditions, rewards, blocks);
        }
    }


}

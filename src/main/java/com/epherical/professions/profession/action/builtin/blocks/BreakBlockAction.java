package com.epherical.professions.profession.action.builtin.blocks;

import com.epherical.professions.profession.action.Action;
import com.epherical.professions.profession.action.ActionType;
import com.epherical.professions.profession.action.Actions;
import com.epherical.professions.profession.conditions.ActionCondition;
import com.epherical.professions.profession.rewards.Reward;
import com.epherical.professions.util.ActionEntry;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import net.minecraft.world.level.block.Block;

import java.util.List;

public class BreakBlockAction extends BlockAbstractAction {

    protected BreakBlockAction(ActionCondition[] conditions, Reward[] rewards, List<ActionEntry<Block>> blocks) {
        super(conditions, rewards, blocks);
    }

    @Override
    public ActionType getType() {
        return Actions.BREAK_BLOCK;
    }

    public static Builder breakBlock() {
        return new Builder();
    }

    public static class Serializer extends BlockAbstractAction.Serializer<BreakBlockAction> {

        @Override
        public BreakBlockAction deserialize(JsonObject object, JsonDeserializationContext context, ActionCondition[] conditions, Reward[] rewards) {
            return new BreakBlockAction(conditions, rewards, deserializeBlocks(object));
        }
    }

    public static class Builder extends BlockAbstractAction.Builder<Builder> {

        @Override
        protected Builder instance() {
            return this;
        }

        @Override
        public Action build() {
            return new BreakBlockAction(this.getConditions(), this.getRewards(), blocks);
        }
    }


}

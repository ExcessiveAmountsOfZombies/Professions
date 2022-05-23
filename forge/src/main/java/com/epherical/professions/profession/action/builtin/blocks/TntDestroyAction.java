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

public class TntDestroyAction extends BlockAbstactAction {


    protected TntDestroyAction(ActionCondition[] conditions, Reward[] rewards, List<ActionEntry<Block>> blockList) {
        super(conditions, rewards, blockList);
    }

    @Override
    public ActionType getType() {
        return Actions.TNT_DESTROY.get();
    }

    public static Builder tntDestroy() {
        return new Builder();
    }

    public static class Serializer extends BlockAbstactAction.Serializer<TntDestroyAction> {

        @Override
        public TntDestroyAction deserialize(JsonObject object, JsonDeserializationContext context, ActionCondition[] conditions, Reward[] rewards) {
            return new TntDestroyAction(conditions, rewards, deserializeBlocks(object));
        }
    }

    public static class Builder extends BlockAbstactAction.Builder<Builder> {

        @Override
        protected Builder instance() {
            return this;
        }

        @Override
        public Action build() {
            return new TntDestroyAction(this.getConditions(), this.getRewards(), blocks);
        }
    }
}

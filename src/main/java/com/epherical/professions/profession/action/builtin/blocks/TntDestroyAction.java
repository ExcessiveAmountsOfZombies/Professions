package com.epherical.professions.profession.action.builtin.blocks;

import com.epherical.professions.profession.action.ActionType;
import com.epherical.professions.profession.action.Actions;
import com.epherical.professions.profession.conditions.ActionCondition;
import com.epherical.professions.profession.rewards.Reward;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import net.minecraft.world.level.block.Block;

import java.util.List;

public class TntDestroyAction extends BasicBlockAbstractAction {


    protected TntDestroyAction(ActionCondition[] conditions, Reward[] rewards, List<Block> blockList) {
        super(conditions, rewards, blockList);
    }

    @Override
    public ActionType getType() {
        return Actions.TNT_DESTROY;
    }

    public static class Serializer extends BasicBlockAbstractAction.Serializer<TntDestroyAction> {

        @Override
        public TntDestroyAction deserialize(JsonObject object, JsonDeserializationContext context, ActionCondition[] conditions, Reward[] rewards) {
            return new TntDestroyAction(conditions, rewards, deserializeBlocks(object));
        }
    }
}

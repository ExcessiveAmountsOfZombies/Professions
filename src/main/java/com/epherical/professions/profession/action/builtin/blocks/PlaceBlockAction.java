package com.epherical.professions.profession.action.builtin.blocks;

import com.epherical.professions.profession.action.ActionType;
import com.epherical.professions.profession.action.Actions;
import com.epherical.professions.profession.conditions.ActionCondition;
import com.epherical.professions.profession.rewards.Reward;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import net.minecraft.world.level.block.Block;

import java.util.List;

public class PlaceBlockAction extends BasicBlockAbstractAction {


    protected PlaceBlockAction(ActionCondition[] conditions, Reward[] rewards, List<Block> blocks) {
        super(conditions, rewards, blocks);
    }

    @Override
    public ActionType getType() {
        return Actions.PLACE_BLOCK;
    }

    public static class Serializer extends BasicBlockAbstractAction.Serializer<PlaceBlockAction> {

        @Override
        public PlaceBlockAction deserialize(JsonObject object, JsonDeserializationContext context, ActionCondition[] conditions, Reward[] rewards) {
            return new PlaceBlockAction(conditions, rewards, deserializeBlocks(object));
        }
    }
}
